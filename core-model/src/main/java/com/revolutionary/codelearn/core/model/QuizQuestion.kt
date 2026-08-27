package com.revolutionary.codelearn.core.model

data class QuizQuestion(
    val id: String,
    val language: Language,
    val trackId: String,
    val prompt: String,
    val choices: List<String>,
    val correctIndex: Int,
    val explanation: String,
)
