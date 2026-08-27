package com.revolutionary.codelearn.engine.lua

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LuaExecutionEngineTest {

    private val engine = LuaExecutionEngine()

    @Test
    fun `print writes to stdout`() = runBlocking {
        val result = engine.run("print(\"Hello, World!\")")

        assertEquals("Hello, World!\n", result.stdout)
        assertEquals("", result.stderr)
        assertEquals(0, result.exitCode)
        assertTrue(!result.timedOut)
    }

    @Test
    fun `unassigned local variable is nil`() = runBlocking {
        val result = engine.run("local playerStatus\nprint(playerStatus)")

        assertEquals("nil\n", result.stdout)
    }

    @Test
    fun `type of a string is string`() = runBlocking {
        val result = engine.run("print(type(\"Hello\"))")

        assertEquals("string\n", result.stdout)
    }

    @Test
    fun `syntax error is reported without crashing`() = runBlocking {
        val result = engine.run("this is not valid lua (((")

        assertTrue(result.stderr.isNotBlank())
        assertEquals(1, result.exitCode)
    }
}
