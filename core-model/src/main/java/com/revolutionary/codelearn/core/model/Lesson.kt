package com.revolutionary.codelearn.core.model

data class Lesson(
    val id: String,
    val language: Language,
    val trackId: String,
    val title: String,
    val referenceMarkdown: String,
    val challengeMarkdown: String,
    val starterCode: String,
    val solutionCode: String,
    val hints: List<String>,
)
