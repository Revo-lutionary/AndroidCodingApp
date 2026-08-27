package com.revolutionary.codelearn.engine.lua

import com.revolutionary.codelearn.core.execution.ExecutionEngine
import com.revolutionary.codelearn.core.model.ExecutionResult
import com.revolutionary.codelearn.core.model.Language
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import org.luaj.vm2.LuaError
import org.luaj.vm2.lib.jse.JsePlatform
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import javax.inject.Inject

/**
 * Runs Lua source with LuaJ (a pure-JVM Lua interpreter, no NDK needed).
 * This is an interim engine: it targets standard Lua 5.1 semantics, not
 * Luau specifically — swapping in the real Luau VM via JNI is tracked as
 * later follow-up work once on-device Lua execution needs Luau-only features.
 *
 * Execution runs on a background thread rather than a cancellable coroutine
 * because LuaJ's interpreter loop isn't cooperative: a runaway script (e.g.
 * an infinite loop) can't be interrupted mid-instruction. On timeout we stop
 * waiting and report timedOut, but the underlying thread is abandoned rather
 * than force-killed.
 */
class LuaExecutionEngine @Inject constructor() : ExecutionEngine {

    override val language: Language = Language.LUA

    override suspend fun run(source: String, timeoutMillis: Long): ExecutionResult {
        val deferred = CompletableDeferred<ExecutionResult>()

        val thread = Thread({
            val outputStream = ByteArrayOutputStream()
            val globals = JsePlatform.standardGlobals()
            globals.STDOUT = PrintStream(outputStream, true)

            val result = try {
                val chunk = globals.load(source, "playground")
                chunk.call()
                ExecutionResult(
                    stdout = outputStream.toString(),
                    stderr = "",
                    exitCode = 0,
                    timedOut = false,
                )
            } catch (e: LuaError) {
                ExecutionResult(
                    stdout = outputStream.toString(),
                    stderr = e.message ?: "Lua error",
                    exitCode = 1,
                    timedOut = false,
                )
            } catch (e: Exception) {
                ExecutionResult(
                    stdout = outputStream.toString(),
                    stderr = e.message ?: e.javaClass.simpleName,
                    exitCode = 1,
                    timedOut = false,
                )
            }
            deferred.complete(result)
        }, "lua-playground-exec")
        thread.isDaemon = true
        thread.start()

        return withTimeoutOrNull(timeoutMillis) { deferred.await() }
            ?: ExecutionResult(
                stdout = "",
                stderr = "Execution timed out after ${timeoutMillis}ms — check for an infinite loop.",
                exitCode = null,
                timedOut = true,
            )
    }
}
