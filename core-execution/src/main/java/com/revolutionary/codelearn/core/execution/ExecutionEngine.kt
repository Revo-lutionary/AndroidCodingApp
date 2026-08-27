package com.revolutionary.codelearn.core.execution

import com.revolutionary.codelearn.core.model.ExecutionResult
import com.revolutionary.codelearn.core.model.Language

/**
 * Contract every on-device language runtime (Python/Chaquopy, Lua/Luau via JNI,
 * C++ via a bundled Clang subprocess) implements, so the Playground UI stays
 * engine-agnostic. Implementations must enforce [timeoutMillis] themselves,
 * since learner code (infinite loops) can't be trusted to terminate on its own.
 */
interface ExecutionEngine {
    val language: Language

    suspend fun run(source: String, timeoutMillis: Long = DEFAULT_TIMEOUT_MILLIS): ExecutionResult

    companion object {
        const val DEFAULT_TIMEOUT_MILLIS = 8_000L
    }
}
