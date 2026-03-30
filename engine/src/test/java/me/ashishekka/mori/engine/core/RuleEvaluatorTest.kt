package me.ashishekka.mori.engine.core

import me.ashishekka.mori.engine.core.models.MoriLayer
import me.ashishekka.mori.engine.core.models.RenderProperty
import me.ashishekka.mori.engine.core.util.OpCode
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.*

class RuleEvaluatorTest {

    private val evaluator = RuleEvaluator(maxStackSize = 32)
    private val state = MoriEngineState()
    private val signals = floatArrayOf(1.1f, 2.2f, 3.3f)

    @Test
    fun `evaluateLayer should populate buffer correctly`() {
        val layer = MoriLayer(id = 1)
        
        // Define rules for X and Y
        val xRule = intArrayOf(OpCode.PUSH_CONST, 100.5f.toBits())
        val yRule = intArrayOf(OpCode.PUSH_CONST, 200.5f.toBits())
        
        layer.propertyRules[RenderProperty.INDEX_X] = xRule
        layer.propertyRules[RenderProperty.INDEX_Y] = yRule
        
        evaluator.evaluateLayer(layer, state, signals)
        
        assertEquals(100.5f, layer.propertyBuffer[RenderProperty.INDEX_X], 1e-6f)
        assertEquals(200.5f, layer.propertyBuffer[RenderProperty.INDEX_Y], 1e-6f)
        assertEquals(0f, layer.propertyBuffer[RenderProperty.INDEX_SCALE_X], 1e-6f) // No rule
    }

    @Test
    fun `test empty bytecode`() {
        assertEquals(0f, evaluator.evaluate(intArrayOf(), state, signals), 1e-6f)
    }

    @Test
    fun `test stack underflow returns zero`() {
        // Execute ADD on empty stack
        val bytecode = intArrayOf(OpCode.ADD)
        assertEquals(0f, evaluator.evaluate(bytecode, state, signals), 1e-6f)
    }

    @Test
    fun `test push const and get time`() {
        state.setFieldValue(MoriEngineStateIndices.FACT_TIME_SECONDS, 42.5f)
        val bytecode = intArrayOf(
            OpCode.PUSH_CONST, 10.5f.toBits(),
            OpCode.GET_TIME,
            OpCode.ADD
        )
        assertEquals(53.0f, evaluator.evaluate(bytecode, state, signals), 1e-6f)
    }

    @Test
    fun `test all state indices`() {
        for (i in 0 until MoriEngineStateIndices.BUFFER_SIZE) {
            val value = i * 0.1f
            state.setFieldValue(i, value)
            
            val bytecode = intArrayOf(OpCode.GET_STATE, i)
            assertEquals("Failed at index $i", value, evaluator.evaluate(bytecode, state, signals), 1e-6f)
        }
        
        // Test invalid index
        assertEquals(0f, evaluator.evaluate(intArrayOf(OpCode.GET_STATE, 99), state, signals), 1e-6f)
    }

    @Test
    fun `test get signal`() {
        // Valid signal
        assertEquals(1.1f, evaluator.evaluate(intArrayOf(OpCode.GET_SIGNAL, 0), state, signals), 1e-6f)
        assertEquals(3.3f, evaluator.evaluate(intArrayOf(OpCode.GET_SIGNAL, 2), state, signals), 1e-6f)
        // Invalid signals
        assertEquals(0f, evaluator.evaluate(intArrayOf(OpCode.GET_SIGNAL, -1), state, signals), 1e-6f)
        assertEquals(0f, evaluator.evaluate(intArrayOf(OpCode.GET_SIGNAL, 8), state, signals), 1e-6f)
    }

    @Test
    fun `test binary math`() {
        val cases = listOf(
            Triple(OpCode.ADD, 10f, 5f) to 15f,
            Triple(OpCode.SUB, 10f, 5f) to 5f,
            Triple(OpCode.MUL, 10f, 5f) to 50f,
            Triple(OpCode.DIV, 10f, 5f) to 2f,
            Triple(OpCode.DIV, 10f, 0f) to 0f, // Div by zero
            Triple(OpCode.MOD, 10f, 3f) to 1f,
            Triple(OpCode.MOD, 10f, 0f) to 0f  // Mod by zero
        )

        for ((input, expected) in cases) {
            val (op, a, b) = input
            val bytecode = intArrayOf(
                OpCode.PUSH_CONST, a.toBits(),
                OpCode.PUSH_CONST, b.toBits(),
                op
            )
            assertEquals("Op $op failed", expected, evaluator.evaluate(bytecode, state, signals), 1e-6f)
        }
    }

    @Test
    fun `test unary math`() {
        assertEquals(sin(1f), evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 1f.toBits(), OpCode.SIN), state, signals), 1e-6f)
        assertEquals(cos(1f), evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 1f.toBits(), OpCode.COS), state, signals), 1e-6f)
        assertEquals(5f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, (-5f).toBits(), OpCode.ABS), state, signals), 1e-6f)
        assertEquals(3f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 9f.toBits(), OpCode.SQRT), state, signals), 1e-6f)
        assertEquals(0f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, (-1f).toBits(), OpCode.SQRT), state, signals), 1e-6f)
    }

    @Test
    fun `test macros`() {
        // CLAMP: value=10, min=0, max=5 -> 5
        val clamp = intArrayOf(OpCode.PUSH_CONST, 10f.toBits(), OpCode.PUSH_CONST, 0f.toBits(), OpCode.PUSH_CONST, 5f.toBits(), OpCode.CLAMP)
        assertEquals(5f, evaluator.evaluate(clamp, state, signals), 1e-6f)

        // CLAMP Robustness: Swap min/max (min=5, max=0) should still work
        val clampRobust = intArrayOf(OpCode.PUSH_CONST, 10f.toBits(), OpCode.PUSH_CONST, 5f.toBits(), OpCode.PUSH_CONST, 0f.toBits(), OpCode.CLAMP)
        assertEquals(5f, evaluator.evaluate(clampRobust, state, signals), 1e-6f)

        // STEP: value=0.6, threshold=0.5 -> 1.0
        val stepTrue = intArrayOf(OpCode.PUSH_CONST, 0.6f.toBits(), OpCode.PUSH_CONST, 0.5f.toBits(), OpCode.STEP)
        assertEquals(1f, evaluator.evaluate(stepTrue, state, signals), 1e-6f)
        val stepFalse = intArrayOf(OpCode.PUSH_CONST, 0.4f.toBits(), OpCode.PUSH_CONST, 0.5f.toBits(), OpCode.STEP)
        assertEquals(0f, evaluator.evaluate(stepFalse, state, signals), 1e-6f)

        // LERP: start=10, end=20, t=0.5 -> 15
        val lerp = intArrayOf(OpCode.PUSH_CONST, 10f.toBits(), OpCode.PUSH_CONST, 20f.toBits(), OpCode.PUSH_CONST, 0.5f.toBits(), OpCode.LERP)
        assertEquals(15f, evaluator.evaluate(lerp, state, signals), 1e-6f)

        // REMAP (Zero Range safety)
        val remapZero = intArrayOf(OpCode.PUSH_CONST, 5f.toBits(), OpCode.PUSH_CONST, 1f.toBits(), OpCode.PUSH_CONST, 1f.toBits(), OpCode.PUSH_CONST, 10f.toBits(), OpCode.PUSH_CONST, 20f.toBits(), OpCode.REMAP)
        assertEquals(10f, evaluator.evaluate(remapZero, state, signals), 1e-6f)

        // REMAP (Inverse Mapping): in(0..1) to out(1..0)
        val remapInverse = intArrayOf(
            OpCode.PUSH_CONST, 0.8f.toBits(), // value
            OpCode.PUSH_CONST, 0f.toBits(),   // inLow
            OpCode.PUSH_CONST, 1f.toBits(),   // inHigh
            OpCode.PUSH_CONST, 1f.toBits(),   // outLow
            OpCode.PUSH_CONST, 0f.toBits(),   // outHigh
            OpCode.REMAP
        )
        assertEquals(0.2f, evaluator.evaluate(remapInverse, state, signals), 1e-6f)
    }

    @Test
    fun `test oscillate parameters`() {
        state.setFieldValue(MoriEngineStateIndices.FACT_TIME_SECONDS, 0f)
        val bc0 = intArrayOf(
            OpCode.PUSH_CONST, 10f.toBits(), // center
            OpCode.PUSH_CONST, 5f.toBits(),  // amp
            OpCode.PUSH_CONST, 2f.toBits(),  // speed
            OpCode.PUSH_CONST, (PI/2).toFloat().toBits(), // phase
            OpCode.OSCILLATE
        )
        assertEquals(15f, evaluator.evaluate(bc0, state, signals), 1e-6f)

        state.setFieldValue(MoriEngineStateIndices.FACT_TIME_SECONDS, (PI/4).toFloat())
        assertEquals(10f, evaluator.evaluate(bc0, state, signals), 1e-6f)
    }

    @Test
    fun `test logic truth table`() {
        // IF_GT
        state.setFieldValue(MoriEngineStateIndices.FACT_BATTERY_LEVEL, 0.8f)
        val ifGtThen = intArrayOf(
            OpCode.GET_STATE, MoriEngineStateIndices.FACT_BATTERY_LEVEL,
            OpCode.PUSH_CONST, 0.5f.toBits(), OpCode.PUSH_CONST, 1f.toBits(), OpCode.PUSH_CONST, 0f.toBits(),
            OpCode.IF_GT
        )
        assertEquals(1f, evaluator.evaluate(ifGtThen, state, signals), 1e-6f)
        
        val ifGtElse = intArrayOf(
            OpCode.GET_STATE, MoriEngineStateIndices.FACT_BATTERY_LEVEL,
            OpCode.PUSH_CONST, 0.9f.toBits(), OpCode.PUSH_CONST, 1f.toBits(), OpCode.PUSH_CONST, 0f.toBits(),
            OpCode.IF_GT
        )
        assertEquals(0f, evaluator.evaluate(ifGtElse, state, signals), 1e-6f)

        // IF_GT Boundary (Equal)
        val ifGtBound = intArrayOf(
            OpCode.GET_STATE, MoriEngineStateIndices.FACT_BATTERY_LEVEL,
            OpCode.PUSH_CONST, 0.8f.toBits(), OpCode.PUSH_CONST, 1f.toBits(), OpCode.PUSH_CONST, 0f.toBits(),
            OpCode.IF_GT
        )
        assertEquals(0f, evaluator.evaluate(ifGtBound, state, signals), 1e-6f)

        // AND (Full Truth Table)
        assertEquals(1f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 1f.toBits(), OpCode.PUSH_CONST, 1f.toBits(), OpCode.AND), state, signals), 1e-6f)
        assertEquals(0f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 1f.toBits(), OpCode.PUSH_CONST, 0f.toBits(), OpCode.AND), state, signals), 1e-6f)
        assertEquals(0f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 0f.toBits(), OpCode.PUSH_CONST, 1f.toBits(), OpCode.AND), state, signals), 1e-6f)
        assertEquals(0f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 0f.toBits(), OpCode.PUSH_CONST, 0f.toBits(), OpCode.AND), state, signals), 1e-6f)
        
        // OR (Full Truth Table)
        assertEquals(1f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 1f.toBits(), OpCode.PUSH_CONST, 1f.toBits(), OpCode.OR), state, signals), 1e-6f)
        assertEquals(1f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 1f.toBits(), OpCode.PUSH_CONST, 0f.toBits(), OpCode.OR), state, signals), 1e-6f)
        assertEquals(1f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 0f.toBits(), OpCode.PUSH_CONST, 1f.toBits(), OpCode.OR), state, signals), 1e-6f)
        assertEquals(0f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 0f.toBits(), OpCode.PUSH_CONST, 0f.toBits(), OpCode.OR), state, signals), 1e-6f)
    }

    @Test
    fun `test atmosphere and noise`() {
        // NOISE (strictly positive)
        val n1 = evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 1.0f.toBits(), OpCode.NOISE), state, signals)
        assert(n1 >= 0f)
        val n2 = evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 4.0f.toBits(), OpCode.NOISE), state, signals)
        assert(n2 >= 0f)

        // MIX_OKLAB
        val red = 0xFFFF0000.toInt().toFloat()
        val blue = 0xFF0000FF.toInt().toFloat()
        assertEquals(red, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, red.toBits(), OpCode.PUSH_CONST, blue.toBits(), OpCode.PUSH_CONST, 0.0f.toBits(), OpCode.MIX_OKLAB), state, signals), 1e-6f)
        assertEquals(blue, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, red.toBits(), OpCode.PUSH_CONST, blue.toBits(), OpCode.PUSH_CONST, 1.0f.toBits(), OpCode.MIX_OKLAB), state, signals), 1e-6f)
        
        val resultMid = evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, red.toBits(), OpCode.PUSH_CONST, blue.toBits(), OpCode.PUSH_CONST, 0.5f.toBits(), OpCode.MIX_OKLAB), state, signals).toInt()
        assertEquals(0xFF43165C.toInt(), resultMid)
    }

    @Test
    fun `test unknown opcode safety`() {
        assertEquals(0f, evaluator.evaluate(intArrayOf(0x99), state, signals), 1e-6f)
        assertEquals(10f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 10f.toBits(), 0x99), state, signals), 1e-6f)
    }

    @Test
    fun `test all easing functions`() {
        assertEquals(0.5f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 0.5f.toBits(), OpCode.EASE_IN_OUT), state, signals), 1e-6f)
        assertEquals(0f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 0f.toBits(), OpCode.EASE_BACK), state, signals), 1e-6f)
        assertEquals(1f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 1f.toBits(), OpCode.EASE_BACK), state, signals), 1e-6f)
        assertEquals(0f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 0f.toBits(), OpCode.EASE_ELASTIC), state, signals), 1e-6f)
        assertEquals(1f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 1f.toBits(), OpCode.EASE_ELASTIC), state, signals), 1e-6f)
        val elasticMid = evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 0.5f.toBits(), OpCode.EASE_ELASTIC), state, signals)
        assertEquals(1.0162f, elasticMid, 0.001f)
    }

    @Test
    fun `test stack overflow safety`() {
        val bytecode = IntArray(100)
        for (i in 0 until 50) {
            bytecode[i * 2] = OpCode.PUSH_CONST
            bytecode[i * 2 + 1] = i.toFloat().toBits()
        }
        assertEquals(31f, evaluator.evaluate(bytecode, state, signals), 1e-6f)
    }

    @Test
    fun `test safety try-catch`() {
        assertEquals(0f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST), state, signals), 1e-6f)
    }
}
