package me.ashishekka.mori.engine.core

import me.ashishekka.mori.engine.core.util.OpCode
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.sin

class RuleEvaluatorTest {

    private val evaluator = RuleEvaluator()
    private val state = MoriEngineState()
    private val signals = FloatArray(8)

    @Test
    fun `test basic arithmetic`() {
        // (10 + 5) * 2 / 5 = 6
        val bytecode = intArrayOf(
            OpCode.PUSH_CONST, 10f.toBits(),
            OpCode.PUSH_CONST, 5f.toBits(),
            OpCode.ADD,
            OpCode.PUSH_CONST, 2f.toBits(),
            OpCode.MUL,
            OpCode.PUSH_CONST, 5f.toBits(),
            OpCode.DIV
        )

        val result = evaluator.evaluate(bytecode, state, signals)
        assertEquals(6f, result, 1e-6f)
    }

    @Test
    fun `test remap macro`() {
        // Remap 0.5 from (0..1) to (100..200) = 150
        val bytecode = intArrayOf(
            OpCode.PUSH_CONST, 0.5f.toBits(), // value
            OpCode.PUSH_CONST, 0f.toBits(),   // inLow
            OpCode.PUSH_CONST, 1f.toBits(),   // inHigh
            OpCode.PUSH_CONST, 100f.toBits(), // outLow
            OpCode.PUSH_CONST, 200f.toBits(), // outHigh
            OpCode.REMAP
        )

        val result = evaluator.evaluate(bytecode, state, signals)
        assertEquals(150f, result, 1e-6f)
    }

    @Test
    fun `test get state and logic`() {
        state.energyBatteryLevel = 0.8f
        
        // if battery > 0.5 then 1 else 0
        val bytecode = intArrayOf(
            OpCode.GET_STATE, MoriEngineStateIndices.INDEX_BATTERY_LEVEL,
            OpCode.PUSH_CONST, 0.5f.toBits(), // threshold
            OpCode.PUSH_CONST, 1f.toBits(),   // thenVal
            OpCode.PUSH_CONST, 0f.toBits(),   // elseVal
            OpCode.IF_GT
        )

        val result = evaluator.evaluate(bytecode, state, signals)
        assertEquals(1f, result, 1e-6f)
    }

    @Test
    fun `test oscillate macro`() {
        state.timeSeconds = 1.0f
        
        // center=0, amp=1, speed=1, phase=0
        // sin(1*1 + 0) * 1 + 0 = sin(1)
        val bytecode = intArrayOf(
            OpCode.PUSH_CONST, 0f.toBits(), // center
            OpCode.PUSH_CONST, 1f.toBits(), // amp
            OpCode.PUSH_CONST, 1f.toBits(), // speed
            OpCode.PUSH_CONST, 0f.toBits(), // phase
            OpCode.OSCILLATE
        )

        val result = evaluator.evaluate(bytecode, state, signals)
        assertEquals(sin(1.0f), result, 1e-6f)
    }

    @Test
    fun `test division by zero safety`() {
        val bytecode = intArrayOf(
            OpCode.PUSH_CONST, 10f.toBits(),
            OpCode.PUSH_CONST, 0f.toBits(),
            OpCode.DIV
        )

        val result = evaluator.evaluate(bytecode, state, signals)
        assertEquals(0f, result, 1e-6f)
    }

    @Test
    fun `test stack overflow safety`() {
        // Push 40 constants into a 32-slot stack
        val bytecode = IntArray(80)
        for (i in 0 until 40) {
            bytecode[i * 2] = OpCode.PUSH_CONST
            bytecode[i * 2 + 1] = i.toFloat().toBits()
        }

        val result = evaluator.evaluate(bytecode, state, signals)
        // Should return the 32nd element (index 31) because pushes after that were ignored
        assertEquals(31f, result, 1e-6f)
    }

    @Test
    fun `test complex island biome expression`() {
        // Simplified "Trees Sway heavily if notification count is high"
        // sway = oscillate(center=0, amp=notificationCount * 10, speed=2, phase=0)
        state.zenNotificationCount = 0.8f
        state.timeSeconds = 2.0f
        
        val bytecode = intArrayOf(
            OpCode.PUSH_CONST, 0f.toBits(), // center
            OpCode.GET_STATE, MoriEngineStateIndices.INDEX_NOTIFICATION_COUNT,
            OpCode.PUSH_CONST, 10f.toBits(),
            OpCode.MUL, // amp = 8.0
            OpCode.PUSH_CONST, 2f.toBits(), // speed
            OpCode.PUSH_CONST, 0f.toBits(), // phase
            OpCode.OSCILLATE
        )
        
        val result = evaluator.evaluate(bytecode, state, signals)
        val expected = 0f + sin(2.0f * 2f + 0f) * 8.0f
        assertEquals(expected, result, 1e-6f)
    }
}
