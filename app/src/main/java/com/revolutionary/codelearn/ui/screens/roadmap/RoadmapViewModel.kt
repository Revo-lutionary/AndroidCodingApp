package com.revolutionary.codelearn.ui.screens.roadmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revolutionary.codelearn.core.curriculum.CurriculumRepository
import com.revolutionary.codelearn.core.data.LessonStatus
import com.revolutionary.codelearn.core.data.ProgressRepository
import com.revolutionary.codelearn.core.model.Language
import com.revolutionary.codelearn.core.model.RoadmapNode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class RoadmapNodeState { LOCKED, CURRENT, COMPLETED }

data class RoadmapUiNode(
    val node: RoadmapNode,
    val state: RoadmapNodeState,
)

data class RoadmapUiState(
    val isLoading: Boolean = true,
    val language: Language? = null,
    val trackId: String = "",
    val trackTitle: String = "",
    val uiNodes: List<RoadmapUiNode> = emptyList(),
)

@HiltViewModel
class RoadmapViewModel @Inject constructor(
    private val curriculumRepository: CurriculumRepository,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoadmapUiState())
    val uiState: StateFlow<RoadmapUiState> = _uiState.asStateFlow()

    fun load(languageId: String) {
        viewModelScope.launch {
            val language = Language.fromId(languageId)
            val track = curriculumRepository.loadTracks().firstOrNull { it.language == language }
            if (track == null) {
                _uiState.value = RoadmapUiState(isLoading = false, language = language)
                return@launch
            }
            progressRepository.observeProgress(language).collect { progress ->
                val completedIds = progress
                    .filter { it.status == LessonStatus.COMPLETED.name }
                    .map { it.lessonId }
                    .toSet()
                var currentAssigned = false
                val uiNodes = track.nodes.map { node ->
                    val state = when {
                        node.id in completedIds -> RoadmapNodeState.COMPLETED
                        !currentAssigned -> {
                            currentAssigned = true
                            RoadmapNodeState.CURRENT
                        }
                        else -> RoadmapNodeState.LOCKED
                    }
                    RoadmapUiNode(node, state)
                }
                _uiState.value = RoadmapUiState(
                    isLoading = false,
                    language = language,
                    trackId = track.id,
                    trackTitle = track.title,
                    uiNodes = uiNodes,
                )
            }
        }
    }
}
