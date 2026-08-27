package com.revolutionary.codelearn.core.curriculum

import kotlinx.serialization.Serializable

@Serializable
internal data class ManifestJson(
    val tracks: List<TrackJson>,
)

@Serializable
internal data class TrackJson(
    val id: String,
    val language: String,
    val title: String,
    val nodes: List<NodeJson> = emptyList(),
)

@Serializable
internal data class NodeJson(
    val id: String,
    val type: String,
)

@Serializable
internal data class LessonJson(
    val id: String,
    val title: String,
    val referenceMarkdown: String,
    val challengeMarkdown: String,
    val starterCode: String,
    val solutionCode: String,
    val hints: List<String> = emptyList(),
)

@Serializable
internal data class QuizJson(
    val id: String,
    val prompt: String,
    val choices: List<String>,
    val correctIndex: Int,
    val explanation: String,
)
