package com.revolutionary.codelearn.ui.screens.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revolutionary.codelearn.core.curriculum.CurriculumRepository
import com.revolutionary.codelearn.core.model.Language
import com.revolutionary.codelearn.core.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LessonListUiState(
    val isLoading: Boolean = true,
    val tracks: List<Track> = emptyList(),
)

@HiltViewModel
class LessonListViewModel @Inject constructor(
    private val curriculumRepository: CurriculumRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonListUiState())
    val uiState: StateFlow<LessonListUiState> = _uiState.asStateFlow()

    fun load(languageId: String) {
        viewModelScope.launch {
            val language = Language.fromId(languageId)
            val tracks = curriculumRepository.loadTracks().filter { it.language == language }
            _uiState.value = LessonListUiState(isLoading = false, tracks = tracks)
        }
    }
}
