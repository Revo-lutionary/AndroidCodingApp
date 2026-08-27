package com.revolutionary.codelearn.ui.screens.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revolutionary.codelearn.core.curriculum.CurriculumRepository
import com.revolutionary.codelearn.core.data.ProgressRepository
import com.revolutionary.codelearn.core.model.ExecutionResult
import com.revolutionary.codelearn.core.model.Language
import com.revolutionary.codelearn.core.model.Lesson
import com.revolutionary.codelearn.execution.ExecutionEngineRegistry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LessonUiState(
    val isLoading: Boolean = true,
    val lesson: Lesson? = null,
    val code: String = "",
    val isRunning: Boolean = false,
    val result: ExecutionResult? = null,
    val engineAvailable: Boolean = true,
)

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val curriculumRepository: CurriculumRepository,
    private val progressRepository: ProgressRepository,
    private val executionEngineRegistry: ExecutionEngineRegistry,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    private lateinit var language: Language

    fun load(languageId: String, trackId: String, lessonId: String) {
        viewModelScope.launch {
            language = Language.fromId(languageId)
            val lesson = curriculumRepository.loadLesson(language, trackId, lessonId)
            val engineAvailable = executionEngineRegistry.forLanguage(language) != null
            _uiState.value = LessonUiState(
                isLoading = false,
                lesson = lesson,
                code = lesson.starterCode,
                engineAvailable = engineAvailable,
            )
        }
    }

    fun onCodeChange(code: String) {
        _uiState.value = _uiState.value.copy(code = code)
    }

    fun runCode() {
        val engine = executionEngineRegistry.forLanguage(language)
        if (engine == null) {
            _uiState.value = _uiState.value.copy(
                result = ExecutionResult(
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
            val result = engine.run(_uiState.value.code)
            _uiState.value = _uiState.value.copy(isRunning = false, result = result)
        }
    }

    fun markComplete() {
        val lesson = _uiState.value.lesson ?: return
        viewModelScope.launch {
            progressRepository.markCompleted(lesson.id, language, _uiState.value.code)
        }
    }
}
