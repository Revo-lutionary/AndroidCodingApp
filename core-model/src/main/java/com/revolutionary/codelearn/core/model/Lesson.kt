package com.revolutionary.codelearn.core.model

data class Lesson(
    val id: String,
    val language: Language,
    val trackId: String,
    val title: String,
    val explanationMarkdown: String,
    val starterCode: String,
    val solutionCode: String,
    val hints: List<String>,
)
