package com.revolutionary.codelearn.execution

import com.revolutionary.codelearn.core.execution.ExecutionEngine
import com.revolutionary.codelearn.core.model.Language
import javax.inject.Inject

/** Looks up the [ExecutionEngine] implementation for a given [Language], if one is wired up yet. */
class ExecutionEngineRegistry @Inject constructor(
    private val engines: Set<@JvmSuppressWildcards ExecutionEngine>,
) {
    fun forLanguage(language: Language): ExecutionEngine? = engines.firstOrNull { it.language == language }
}
