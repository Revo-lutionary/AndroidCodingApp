package com.revolutionary.codelearn.ui.screens.ide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revolutionary.codelearn.core.data.IdeTabEntity
import com.revolutionary.codelearn.core.data.IdeTabRepository
import com.revolutionary.codelearn.core.model.ExecutionResult
import com.revolutionary.codelearn.core.model.Language
import com.revolutionary.codelearn.execution.ExecutionEngineRegistry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

private val STARTER_CODE = mapOf(
    Language.PYTHON to "print(\"Hello, World!\")\n",
    Language.LUA to "print(\"Hello, World!\")\n",
    Language.CPP to "#include <iostream>\n\nint main() {\n    std::cout << \"Hello, World!\";\n}\n",
)

data class IdeUiState(
    val tabs: List<IdeTabEntity> = emptyList(),
    val selectedTabId: String? = null,
    val isRunning: Boolean = false,
    val results: Map<String, ExecutionResult> = emptyMap(),
)

/**
 * Tabs (id, title, language, code) persist to Room so they survive the app
 * being closed and reopened, browser-tab style. Code edits are saved with a
 * short debounce so we're not hitting the DB on every keystroke; switching
 * language or opening/closing a tab saves immediately.
 */
@HiltViewModel
class IdeViewModel @Inject constructor(
    private val tabRepository: IdeTabRepository,
    private val executionEngineRegistry: ExecutionEngineRegistry,
) : ViewModel() {

    private val _uiState = MutableStateFlow(IdeUiState())
    val uiState: StateFlow<IdeUiState> = _uiState.asStateFlow()

    private val saveJobs = mutableMapOf<String, Job>()

    init {
        viewModelScope.launch {
            tabRepository.observeAll().collect { storedTabs ->
                val tabs = storedTabs.ifEmpty {
                    val defaultTab = newTab(position = 0)
                    tabRepository.upsert(defaultTab)
                    listOf(defaultTab)
                }
                val selected = _uiState.value.selectedTabId
                    ?.takeIf { id -> tabs.any { it.id == id } }
                    ?: tabs.first().id
                _uiState.value = _uiState.value.copy(tabs = tabs, selectedTabId = selected)
            }
        }
    }

    fun selectTab(id: String) {
        _uiState.value = _uiState.value.copy(selectedTabId = id)
    }

    fun addTab() {
        viewModelScope.launch {
            val tab = newTab(position = _uiState.value.tabs.size)
            tabRepository.upsert(tab)
            _uiState.value = _uiState.value.copy(selectedTabId = tab.id)
        }
    }

    fun closeTab(id: String) {
        if (_uiState.value.tabs.size <= 1) return
        viewModelScope.launch {
            tabRepository.delete(id)
        }
    }

    fun onCodeChange(tabId: String, code: String) {
        val tabs = _uiState.value.tabs.map { if (it.id == tabId) it.copy(code = code) else it }
        _uiState.value = _uiState.value.copy(tabs = tabs)

        saveJobs[tabId]?.cancel()
        saveJobs[tabId] = viewModelScope.launch {
            delay(500)
            tabs.firstOrNull { it.id == tabId }?.let { tabRepository.upsert(it) }
        }
    }

    fun renameTab(tabId: String, newTitle: String) {
        val trimmed = newTitle.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            val tab = _uiState.value.tabs.firstOrNull { it.id == tabId } ?: return@launch
            tabRepository.upsert(tab.copy(title = trimmed))
        }
    }

    /** Moves the tab at [fromIndex] to [toIndex], shifting the others over. */
    fun reorderTabs(fromIndex: Int, toIndex: Int) {
        val tabs = _uiState.value.tabs.toMutableList()
        if (fromIndex !in tabs.indices || toIndex !in tabs.indices) return
        val moved = tabs.removeAt(fromIndex)
        tabs.add(toIndex, moved)
        _uiState.value = _uiState.value.copy(tabs = tabs)
        viewModelScope.launch {
            tabs.forEachIndexed { index, tab ->
                if (tab.position != index) {
                    tabRepository.upsert(tab.copy(position = index))
                }
            }
        }
    }

    fun selectLanguage(tabId: String, language: Language) {
        viewModelScope.launch {
            val tab = _uiState.value.tabs.firstOrNull { it.id == tabId } ?: return@launch
            tabRepository.upsert(
                tab.copy(
                    languageId = language.id,
                    code = STARTER_CODE.getValue(language),
                    title = language.displayName,
                ),
            )
        }
    }

    fun runCode(tabId: String) {
        val tab = _uiState.value.tabs.firstOrNull { it.id == tabId } ?: return
        val language = Language.fromId(tab.languageId)
        val engine = executionEngineRegistry.forLanguage(language)
        if (engine == null) {
            setResult(
                tabId,
                ExecutionResult(
                    stdout = "",
                    stderr = "Running $language code isn't wired up yet — coming in a later update.",
                    exitCode = null,
                    timedOut = false,
                ),
            )
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRunning = true)
            val result = engine.run(tab.code)
            _uiState.value = _uiState.value.copy(isRunning = false)
            setResult(tabId, result)
        }
    }

    private fun setResult(tabId: String, result: ExecutionResult) {
        _uiState.value = _uiState.value.copy(results = _uiState.value.results + (tabId to result))
    }

    private fun newTab(language: Language = Language.LUA, position: Int) = IdeTabEntity(
        id = UUID.randomUUID().toString(),
        title = language.displayName,
        languageId = language.id,
        code = STARTER_CODE.getValue(language),
        position = position,
    )
}
