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
    val lessons: List<String>,
)

@Serializable
internal data class LessonJson(
    val id: String,
    val title: String,
    val explanationMarkdown: String,
    val starterCode: String,
    val solutionCode: String,
    val hints: List<String> = emptyList(),
)
