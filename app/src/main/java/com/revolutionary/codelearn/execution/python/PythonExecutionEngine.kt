package com.revolutionary.codelearn.execution.python

import com.chaquo.python.Python
import com.revolutionary.codelearn.core.execution.ExecutionEngine
import com.revolutionary.codelearn.core.model.ExecutionResult
import com.revolutionary.codelearn.core.model.Language
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

/**
 * Runs Python source via Chaquopy's embedded CPython. Python.start() must
 * already have been called once (see CodeLearnApp.onCreate) before this is
 * used.
 *
 * Like [com.revolutionary.codelearn.engine.lua.LuaExecutionEngine], this runs
 * on a background thread rather than a cancellable coroutine, since a
 * runaway script can't be interrupted mid-execution; on timeout we stop
 * waiting and report timedOut, but the underlying thread is abandoned.
 */
class PythonExecutionEngine @Inject constructor() : ExecutionEngine {

    override val language: Language = Language.PYTHON

    override suspend fun run(source: String, timeoutMillis: Long): ExecutionResult {
        val deferred = CompletableDeferred<ExecutionResult>()

        val thread = Thread({
            val py = Python.getInstance()
            val sys = py.getModule("sys")
            val io = py.getModule("io")
            val stdoutCapture = io.callAttr("StringIO")
            val stderrCapture = io.callAttr("StringIO")
            val originalStdout = sys.get("stdout")
            val originalStderr = sys.get("stderr")
            sys.put("stdout", stdoutCapture)
            sys.put("stderr", stderrCapture)

            val result = try {
                py.builtins.callAttr("exec", source)
                ExecutionResult(
                    stdout = stdoutCapture.callAttr("getvalue").toString(),
                    stderr = stderrCapture.callAttr("getvalue").toString(),
                    exitCode = 0,
                    timedOut = false,
                )
            } catch (e: Exception) {
                ExecutionResult(
                    stdout = stdoutCapture.callAttr("getvalue").toString(),
                    stderr = stderrCapture.callAttr("getvalue").toString().ifBlank {
                        e.message ?: e.javaClass.simpleName
                    },
                    exitCode = 1,
                    timedOut = false,
                )
            } finally {
                sys.put("stdout", originalStdout)
                sys.put("stderr", originalStderr)
            }
            deferred.complete(result)
        }, "python-playground-exec")
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
