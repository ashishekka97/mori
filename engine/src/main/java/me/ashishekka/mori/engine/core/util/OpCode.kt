package me.ashishekka.mori.engine.core.util

/**
 * Defines the primitive "Machine Code" instructions for the Mori Rule Engine (v1.4).
 * 
 * DESIGN PRINCIPLES:
 * 1. All instructions operate on a Float evaluation stack.
 * 2. Instructions are encoded as Ints in a flat array.
 * 3. Supports high-performance atmospheric macros and core arithmetic.
 */
object OpCode {
    // --- 0x00: Data Ingress ---
    const val PUSH_CONST = 0x01
    const val GET_TIME = 0x02
    const val GET_STATE = 0x03
    const val GET_SIGNAL = 0x04

    // --- 0x10: Binary Math ---
    const val ADD = 0x10
    const val SUB = 0x11
    const val MUL = 0x12
    const val DIV = 0x13
    const val MOD = 0x14

    // --- 0x20: Unary Math ---
    const val SIN = 0x20
    const val COS = 0x21
    const val ABS = 0x22
    const val SQRT = 0x23

    // --- 0x30: Macros ---
    const val REMAP = 0x30
    const val CLAMP = 0x31
    const val STEP = 0x32
    const val LERP = 0x33
    const val OSCILLATE = 0x34

    // --- 0x40: Logic ---
    const val IF_GT = 0x40
    const val AND = 0x41
    const val OR = 0x42

    // --- 0x50: Atmosphere ---
    const val NOISE = 0x50
    const val MIX_OKLAB = 0x51

    // --- 0x60: Motion Easing ---
    const val EASE_IN_OUT = 0x60
    const val EASE_BACK = 0x61
    const val EASE_ELASTIC = 0x62
}
