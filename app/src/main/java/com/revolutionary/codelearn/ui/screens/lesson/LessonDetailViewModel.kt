package com.revolutionary.codelearn.ui.screens.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revolutionary.codelearn.core.curriculum.CurriculumRepository
import com.revolutionary.codelearn.core.model.Language
import com.revolutionary.codelearn.core.model.Lesson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LessonDetailUiState(
    val isLoading: Boolean = true,
    val lesson: Lesson? = null,
    val code: String = "",
)

@HiltViewModel
class LessonDetailViewModel @Inject constructor(
    private val curriculumRepository: CurriculumRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonDetailUiState())
    val uiState: StateFlow<LessonDetailUiState> = _uiState.asStateFlow()

    fun load(languageId: String, trackId: String, lessonId: String) {
        viewModelScope.launch {
            val lesson = curriculumRepository.loadLesson(Language.fromId(languageId), trackId, lessonId)
            _uiState.value = LessonDetailUiState(isLoading = false, lesson = lesson, code = lesson.starterCode)
        }
    }

    fun onCodeChange(code: String) {
        _uiState.value = _uiState.value.copy(code = code)
    }
}
