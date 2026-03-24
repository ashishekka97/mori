package me.ashishekka.mori.engine.core.util

/**
 * Defines the primitive "Machine Code" instructions for the Mori Rule Engine.
 * 
 * DESIGN PRINCIPLES:
 * 1. All instructions operate on a Float evaluation stack.
 * 2. Instructions are encoded as Ints in a flat array.
 * 3. Some instructions (PUSH_CONST, GET_STATE) occupy two slots (OpCode + Operand).
 */
object OpCode {
    // --- 0x00: Data Ingress ---
    /** Pushes the next Float from bytecode onto the stack. [OpCode, Float] */
    const val PUSH_CONST = 0x01
    /** Pushes the smooth normalized frame time (seconds) onto the stack. */
    const val GET_TIME = 0x02
    /** Pushes a value from MoriEngineState onto the stack. [OpCode, StateIndex] */
    const val GET_STATE = 0x03
    /** Pushes a pre-calculated global signal onto the stack. [OpCode, SignalIndex] */
    const val GET_SIGNAL = 0x04

    // --- 0x10: Binary Math ---
    /** Pop(b), Pop(a), Push(a + b) */
    const val ADD = 0x10
    /** Pop(b), Pop(a), Push(a - b) */
    const val SUB = 0x11
    /** Pop(b), Pop(a), Push(a * b) */
    const val MUL = 0x12
    /** Pop(b), Pop(a), Push(a / b) */
    const val DIV = 0x13
    /** Pop(b), Pop(a), Push(a % b) */
    const val MOD = 0x14

    // --- 0x20: Unary Math ---
    /** Pop(f), Push(sin(f)) */
    const val SIN = 0x20
    /** Pop(f), Push(cos(f)) */
    const val COS = 0x21
    /** Pop(f), Push(abs(f)) */
    const val ABS = 0x22
    /** Pop(f), Push(sqrt(f)) */
    const val SQRT = 0x23

    // --- 0x30: Macros ---
    /** Pop(oMax, oMin, iMax, iMin, v), Push(remappedValue) */
    const val REMAP = 0x30
    /** Pop(max, min, v), Push(clampedValue) */
    const val CLAMP = 0x31
    /** Pop(edge, v), Push(v > edge ? 1.0 : 0.0) */
    const val STEP = 0x32
    /** Pop(t, b, a), Push(a + (b-a) * t) */
    const val LERP = 0x33

    // --- 0x40: Logic ---
    /** Pop(ifFalse, ifTrue, b, a), Push(a > b ? ifTrue : ifFalse) */
    const val IF_GT = 0x40
    /** Pop(b, a), Push(a == 1.0 && b == 1.0 ? 1.0 : 0.0) */
    const val AND = 0x41
    /** Pop(b, a), Push(a == 1.0 || b == 1.0 ? 1.0 : 0.0) */
    const val OR = 0x42

    // --- 0x50: Atmosphere ---
    /** Pop(seed), Push(coherentNoiseValue) */
    const val NOISE = 0x50
    /** Pop(fraction, colorB, colorA), Push(perceptualMixedColor) */
    const val MIX_OKLAB = 0x51
}
