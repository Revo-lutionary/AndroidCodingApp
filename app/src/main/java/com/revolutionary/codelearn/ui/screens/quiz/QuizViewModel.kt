package com.revolutionary.codelearn.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revolutionary.codelearn.core.curriculum.CurriculumRepository
import com.revolutionary.codelearn.core.data.ProgressRepository
import com.revolutionary.codelearn.core.model.Language
import com.revolutionary.codelearn.core.model.QuizQuestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val isLoading: Boolean = true,
    val quiz: QuizQuestion? = null,
    val selectedIndex: Int? = null,
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val curriculumRepository: CurriculumRepository,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private lateinit var language: Language

    fun load(languageId: String, trackId: String, quizId: String) {
        viewModelScope.launch {
            language = Language.fromId(languageId)
            val quiz = curriculumRepository.loadQuiz(language, trackId, quizId)
            _uiState.value = QuizUiState(isLoading = false, quiz = quiz)
        }
    }

    fun selectChoice(index: Int) {
        if (_uiState.value.selectedIndex != null) return // lock in the first answer
        _uiState.value = _uiState.value.copy(selectedIndex = index)
    }

    fun finish() {
        val quiz = _uiState.value.quiz ?: return
        viewModelScope.launch {
            progressRepository.markCompleted(quiz.id, language, "")
        }
    }
}
