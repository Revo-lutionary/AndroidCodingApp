package com.revolutionary.codelearn.core.model

data class ExecutionResult(
    val stdout: String,
    val stderr: String,
    val exitCode: Int?,
    val timedOut: Boolean,
)
