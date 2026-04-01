package me.ashishekka.mori.engine.core

import me.ashishekka.mori.engine.core.models.MoriLayer
import me.ashishekka.mori.engine.core.models.RenderProperty
import me.ashishekka.mori.engine.core.util.ColorUtils
import me.ashishekka.mori.engine.core.util.OpCode
import kotlin.math.*

/**
 * The high-performance virtual machine for the Mori Rule Engine.
 * Executes pre-compiled [IntArray] bytecode using a pre-allocated stack.
 * 
 * DESIGN PRINCIPLES:
 * 1. 100% Zero-Allocation: No objects are created during the evaluation loop.
 * 2. Stack-Based: All operations pop and push results to a pre-allocated FloatArray.
 * 3. Atomic Safety: Includes guards for division-by-zero and stack overflow.
 */
class RuleEvaluator(private val maxStackSize: Int = 32) {

    private val stack = FloatArray(maxStackSize)
    private var sp = -1 // Stack Pointer

    /**
     * Executes the provided bytecode and returns the final result from the top of the stack.
     * 
     * @param bytecode The machine code to execute.
     * @param state The current real-world state of the machine.
     * @param signals A buffer for inter-layer communication (GET_SIGNAL).
     * @return The final Float result (or 0.0f if bytecode is empty or malformed).
     */
    fun evaluate(
        bytecode: IntArray?,
        state: MoriEngineState,
        signals: FloatArray
    ): Float {
        if (bytecode == null || bytecode.isEmpty()) return 0f

        sp = -1 // Reset stack pointer
        var pc = 0 // Program Counter
        val length = bytecode.size

        try {
            while (pc < length) {
                val opcode = bytecode[pc++]

                when (opcode) {
                    // --- 0x00: Data Ingress ---
                    OpCode.PUSH_CONST -> {
                        // The next Int in bytecode is the Float bits representation
                        push(java.lang.Float.intBitsToFloat(bytecode[pc++]))
                    }
                    OpCode.GET_TIME -> {
                        push(state.getFieldValue(MoriEngineStateIndices.FACT_TIME_SECONDS))
                    }
                    OpCode.GET_STATE -> {
                        val index = bytecode[pc++]
                        push(state.getFieldValue(index))
                    }
                    OpCode.GET_SIGNAL -> {
                        val index = bytecode[pc++]
                        if (index >= 0 && index < signals.size) {
                            push(signals[index])
                        } else {
                            push(0f)
                        }
                    }

                    // --- 0x10: Binary Math ---
                    OpCode.ADD -> {
                        val b = pop()
                        val a = pop()
                        push(a + b)
                    }
                    OpCode.SUB -> {
                        val b = pop()
                        val a = pop()
                        push(a - b)
                    }
                    OpCode.MUL -> {
                        val b = pop()
                        val a = pop()
                        push(a * b)
                    }
                    OpCode.DIV -> {
                        val b = pop()
                        val a = pop()
                        push(if (b != 0f) a / b else 0f)
                    }
                    OpCode.MOD -> {
                        val b = pop()
                        val a = pop()
                        push(if (b != 0f) a % b else 0f)
                    }

                    // --- 0x20: Unary Math ---
                    OpCode.SIN -> push(sin(pop()))
                    OpCode.COS -> push(cos(pop()))
                    OpCode.ABS -> push(abs(pop()))
                    OpCode.SQRT -> {
                        val a = pop()
                        push(if (a >= 0f) sqrt(a) else 0f)
                    }

                    // --- 0x30: Macros ---
                    OpCode.REMAP -> {
                        val outHigh = pop()
                        val outLow = pop()
                        val inHigh = pop()
                        val inLow = pop()
                        val value = pop()
                        
                        val range = inHigh - inLow
                        push(if (abs(range) > 1e-6f) {
                            val t = (value - inLow) / range
                            outLow + t * (outHigh - outLow)
                        } else {
                            outLow
                        })
                    }
                    OpCode.CLAMP -> {
                        val maxArg = pop()
                        val minArg = pop()
                        val value = pop()
                        val realMin = min(minArg, maxArg)
                        val realMax = max(minArg, maxArg)
                        push(value.coerceIn(realMin, realMax))
                    }
                    OpCode.STEP -> {
                        val threshold = pop()
                        val value = pop()
                        push(if (value >= threshold) 1f else 0f)
                    }
                    OpCode.LERP -> {
                        val t = pop()
                        val end = pop()
                        val start = pop()
                        push(start + t * (end - start))
                    }
                    OpCode.OSCILLATE -> {
                        val phase = pop()
                        val speed = pop()
                        val amplitude = pop()
                        val center = pop()
                        val time = state.timeSeconds
                        push(center + sin(time * speed + phase) * amplitude)
                    }

                    // --- 0x40: Logic ---
                    OpCode.IF_GT -> {
                        val elseVal = pop()
                        val thenVal = pop()
                        val threshold = pop()
                        val value = pop()
                        push(if (value > threshold) thenVal else elseVal)
                    }
                    OpCode.AND -> {
                        val b = pop()
                        val a = pop()
                        push(if (a > 0.5f && b > 0.5f) 1f else 0f)
                    }
                    OpCode.OR -> {
                        val b = pop()
                        val a = pop()
                        push(if (a > 0.5f || b > 0.5f) 1f else 0f)
                    }

                    // --- 0x50: Atmosphere ---
                    OpCode.NOISE -> {
                        // High-performance deterministic strictly-positive pseudo-random hash
                        val x = pop()
                        push(abs(sin(x) * 43758.5453123f) % 1.0f)
                    }
                    OpCode.MIX_OKLAB -> {
                        val t = pop()
                        val colorTo = java.lang.Float.floatToRawIntBits(pop())
                        val colorFrom = java.lang.Float.floatToRawIntBits(pop())
                        push(java.lang.Float.intBitsToFloat(ColorUtils.lerpColorOklab(colorFrom, colorTo, t)))
                    }

                    // --- 0x60: Motion Easing ---
                    OpCode.EASE_IN_OUT -> {
                        val t = pop().coerceIn(0f, 1f)
                        push(t * t * (3 - 2 * t))
                    }
                    OpCode.EASE_BACK -> {
                        val t = pop().coerceIn(0f, 1f)
                        val c1 = 1.70158f
                        val c3 = c1 + 1f
                        push(c3 * t * t * t - c1 * t * t)
                    }
                    OpCode.EASE_ELASTIC -> {
                        val t = pop().coerceIn(0f, 1f)
                        val c4 = (2f * PI) / 3f
                        push(if (t == 0f) 0f else if (t == 1f) 1f else {
                            (2.0.pow(-10.0 * t) * sin((t * 10f - 0.75f) * c4) + 1f).toFloat()
                        })
                    }
                }
            }
        } catch (e: Exception) {
            // Fail gracefully in the hot path to avoid engine crashes
            return 0f
        }

        return if (sp >= 0) stack[sp] else 0f
    }

    /**
     * Batch execution of rules for a given [MoriLayer].
     * Writes the resulting properties directly into the [MoriLayer.propertyBuffer].
     * 
     * DESIGN PRINCIPLE:
     * This loop is optimized for 0 allocations and high cache locality.
     */
    fun evaluateLayer(
        layer: MoriLayer,
        state: MoriEngineState,
        signals: FloatArray
    ) {
        val rules = layer.propertyRules
        val buffer = layer.propertyBuffer
        for (i in 0 until RenderProperty.BUFFER_SIZE) {
            val bytecode = rules[i]
            // If there's a rule, update the buffer. If not, the previous value persists.
            if (bytecode != null) {
                buffer[i] = evaluate(bytecode, state, signals)
            }
        }
    }

    private fun push(value: Float) {
        if (sp < maxStackSize - 1) {
            stack[++sp] = value
        }
    }

    private fun pop(): Float {
        return if (sp >= 0) {
            stack[sp--]
        } else {
            0f
        }
    }
}
