package me.ashishekka.mori.biome.compiler

import me.ashishekka.mori.engine.core.util.OpCode
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class ExpressionCompilerTest {

    @Test
    fun `compile should handle simple arithmetic`() {
        val result = ExpressionCompiler.compile("10 + 20")
        val expected = intArrayOf(
            OpCode.PUSH_CONST, 30f.toBits()
        )
        assertArrayEquals(expected, result)
    }

    @Test
    fun `compile should handle operator precedence`() {
        val result = ExpressionCompiler.compile("10 + 2 * 5")
        val expected = intArrayOf(
            OpCode.PUSH_CONST, 20f.toBits()
        )
        assertArrayEquals(expected, result)
    }

    @Test
    fun `compile should handle parentheses`() {
        val result = ExpressionCompiler.compile("(10 + 2) * 5")
        val expected = intArrayOf(
            OpCode.PUSH_CONST, 60f.toBits()
        )
        assertArrayEquals(expected, result)
    }

    @Test
    fun `compile should handle functions`() {
        val result = ExpressionCompiler.compile("sin(time)")
        val expected = intArrayOf(
            OpCode.GET_TIME,
            OpCode.SIN
        )
        assertArrayEquals(expected, result)
    }

    @Test
    fun `compile should handle complex functions with multiple arguments`() {
        val result = ExpressionCompiler.compile("clamp(fact[2], 0, 1)")
        println("ACTUAL RESULT: ${result.toList()}")
        val expected = intArrayOf(
            OpCode.GET_STATE, 2,
            OpCode.PUSH_CONST, 0f.toBits(),
            OpCode.PUSH_CONST, 1f.toBits(),
            OpCode.CLAMP
        )
        assertArrayEquals(expected, result)
    }

    // --- ROBUSTNESS TESTS ---

    @Test
    fun `compile should return empty array for empty or blank strings`() {
        assertArrayEquals(intArrayOf(), ExpressionCompiler.compile(""))
        assertArrayEquals(intArrayOf(), ExpressionCompiler.compile("   "))
        assertArrayEquals(intArrayOf(), ExpressionCompiler.compile(null))
    }

    @Test
    fun `compile should return empty array for mismatched parentheses`() {
        assertArrayEquals(intArrayOf(), ExpressionCompiler.compile("(10 + 2"))
        assertArrayEquals(intArrayOf(), ExpressionCompiler.compile("10 + 2)"))
        assertArrayEquals(intArrayOf(), ExpressionCompiler.compile("sin(time"))
    }

    @Test
    fun `compile should return empty array for malformed logic`() {
        assertArrayEquals(intArrayOf(), ExpressionCompiler.compile("10 + * 5"))
        assertArrayEquals(intArrayOf(), ExpressionCompiler.compile("sin(,)"))
    }

    @Test
    fun `compile should push zero for unknown variables`() {
        val result = ExpressionCompiler.compile("unknown_var + 10")
        val expected = intArrayOf(
            OpCode.PUSH_CONST, 10f.toBits()
        )
        assertArrayEquals(expected, result)
    }
}
