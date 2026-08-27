package com.revolutionary.codelearn.ui.screens.ide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revolutionary.codelearn.core.model.ExecutionResult
import com.revolutionary.codelearn.core.model.Language
import com.revolutionary.codelearn.execution.ExecutionEngineRegistry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private val STARTER_CODE = mapOf(
    Language.PYTHON to "print(\"Hello, World!\")\n",
    Language.LUA to "print(\"Hello, World!\")\n",
    Language.CPP to "#include <iostream>\n\nint main() {\n    std::cout << \"Hello, World!\";\n}\n",
)

data class IdeUiState(
    val language: Language = Language.LUA,
    val code: String = STARTER_CODE.getValue(Language.LUA),
    val isRunning: Boolean = false,
    val result: ExecutionResult? = null,
    val engineAvailable: Boolean = true,
)

@HiltViewModel
class IdeViewModel @Inject constructor(
    private val executionEngineRegistry: ExecutionEngineRegistry,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        IdeUiState(engineAvailable = executionEngineRegistry.forLanguage(Language.LUA) != null),
    )
    val uiState: StateFlow<IdeUiState> = _uiState.asStateFlow()

    fun selectLanguage(language: Language) {
        _uiState.value = IdeUiState(
            language = language,
            code = STARTER_CODE.getValue(language),
            engineAvailable = executionEngineRegistry.forLanguage(language) != null,
        )
    }

    fun onCodeChange(code: String) {
        _uiState.value = _uiState.value.copy(code = code)
    }

    fun runCode() {
        val state = _uiState.value
        val engine = executionEngineRegistry.forLanguage(state.language)
        if (engine == null) {
            _uiState.value = state.copy(
                result = ExecutionResult(
                    stdout = "",
                    stderr = "Running ${state.language} code isn't wired up yet — coming in a later update.",
                    exitCode = null,
                    timedOut = false,
                ),
            )
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRunning = true)
            val result = engine.run(_uiState.value.code)
            _uiState.value = _uiState.value.copy(isRunning = false, result = result)
        }
    }
}
