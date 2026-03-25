package me.ashishekka.mori.engine.core

import me.ashishekka.mori.engine.core.util.OpCode
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.*

class RuleEvaluatorTest {

    private val evaluator = RuleEvaluator(maxStackSize = 32)
    private val state = MoriEngineState()
    private val signals = floatArrayOf(1.1f, 2.2f, 3.3f)

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
        state.timeSeconds = 42.5f
        val bytecode = intArrayOf(
            OpCode.PUSH_CONST, 10.5f.toBits(),
            OpCode.GET_TIME,
            OpCode.ADD
        )
        assertEquals(53.0f, evaluator.evaluate(bytecode, state, signals), 1e-6f)
    }

    @Test
    fun `test all state indices`() {
        state.timeSeconds = 0.1f
        state.chronosSunAltitude = 0.2f
        state.energyBatteryLevel = 0.3f
        state.energyIsCharging = true // 1.0
        state.vitalityStepsProgress = 0.5f
        state.energyThermalStress = 0.6f
        state.zenSocialNoise = 0.7f
        state.atmosLightLevel = 0.8f
        state.zenDailyScreenTime = 0.9f
        state.vitalityRestQuality = 1.0f
        state.atmosKpIndex = 1.1f
        state.mediaPulse = 1.2f
        state.chronosAlarmDistance = 1.3f
        state.energyChargingSpeed = 1.4f
        state.zenNotificationCount = 1.5f
        state.atmosTemperature = 1.6f

        for (i in 0..15) {
            val bytecode = intArrayOf(OpCode.GET_STATE, i)
            val expected = state.getFieldValue(i)
            assertEquals("Failed at index $i", expected, evaluator.evaluate(bytecode, state, signals), 1e-6f)
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
    }

    @Test
    fun `test logic truth table`() {
        // AND
        assertEquals(1f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 1f.toBits(), OpCode.PUSH_CONST, 1f.toBits(), OpCode.AND), state, signals), 1e-6f)
        assertEquals(0f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 1f.toBits(), OpCode.PUSH_CONST, 0f.toBits(), OpCode.AND), state, signals), 1e-6f)
        
        // OR
        assertEquals(1f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 1f.toBits(), OpCode.PUSH_CONST, 0f.toBits(), OpCode.OR), state, signals), 1e-6f)
        assertEquals(0f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 0f.toBits(), OpCode.PUSH_CONST, 0f.toBits(), OpCode.OR), state, signals), 1e-6f)
    }

    @Test
    fun `test atmosphere and noise`() {
        // NOISE (deterministic)
        val n1 = evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 1.0f.toBits(), OpCode.NOISE), state, signals)
        val n2 = evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 1.0f.toBits(), OpCode.NOISE), state, signals)
        assertEquals(n1, n2, 1e-6f)

        // MIX_OKLAB
        val red = 0xFFFF0000.toInt().toFloat()
        val blue = 0xFF0000FF.toInt().toFloat()
        // t=0.0 -> Red
        val mix0 = intArrayOf(OpCode.PUSH_CONST, red.toBits(), OpCode.PUSH_CONST, blue.toBits(), OpCode.PUSH_CONST, 0.0f.toBits(), OpCode.MIX_OKLAB)
        assertEquals(red, evaluator.evaluate(mix0, state, signals), 1e-6f)
    }

    @Test
    fun `test all easing functions`() {
        // Ease In Out
        assertEquals(0.5f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 0.5f.toBits(), OpCode.EASE_IN_OUT), state, signals), 1e-6f)
        
        // Ease Back (Boundary)
        assertEquals(0f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 0f.toBits(), OpCode.EASE_BACK), state, signals), 1e-6f)
        assertEquals(1f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 1f.toBits(), OpCode.EASE_BACK), state, signals), 1e-6f)

        // Ease Elastic (Boundary)
        assertEquals(0f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 0f.toBits(), OpCode.EASE_ELASTIC), state, signals), 1e-6f)
        assertEquals(1f, evaluator.evaluate(intArrayOf(OpCode.PUSH_CONST, 1f.toBits(), OpCode.EASE_ELASTIC), state, signals), 1e-6f)
        // Mid-point
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
        // Result should be the 32nd element (index 31)
        assertEquals(31f, evaluator.evaluate(bytecode, state, signals), 1e-6f)
    }

    @Test
    fun `test safety try-catch`() {
        // Malformed bytecode (PUSH_CONST but no value following)
        val bytecode = intArrayOf(OpCode.PUSH_CONST)
        assertEquals(0f, evaluator.evaluate(bytecode, state, signals), 1e-6f)
    }
}
