package me.ashishekka.mori.engine.core.util

import me.ashishekka.mori.engine.core.MoriEngineState

/**
 * A dedicated utility to map atmospheric world signals to UI theme tokens.
 * This implementation uses continuous color ramps to ensure smooth transitions.
 */
object AtmosphericThemeMapper {

    fun updatePalette(state: MoriEngineState) {
        val sun = state.chronosSunAltitude
        
        // 1. Determine Perceptual "Dark" state
        state.isDarkState = sun <= 0.2f

        // 2. Calculate Continuous Foundation (Background)
        // Lerp: Deep Navy -> Warm Grey -> Sky Blue -> White
        state.dominantFoundationColor = when {
            sun < -0.5f -> 0xFF0A0E14.toInt() // Deep Midnight
            sun < 0f -> lerpColor(0xFF0A0E14.toInt(), 0xFF1A1C2E.toInt(), (sun + 0.5f) * 2f) // Midnight to Dusk
            sun < 0.5f -> lerpColor(0xFF1A1C2E.toInt(), 0xFFF5F5F5.toInt(), sun * 2f) // Dusk to Morning
            else -> 0xFFFFFFFF.toInt() // Broad Daylight
        }

        // 3. Calculate Continuous Accent
        // Lerp: Electric Violet -> Sunset Amber -> Vibrant Orange
        state.dominantAccentColor = when {
            sun < 0f -> lerpColor(0xFFBB86FC.toInt(), 0xFFFF7043.toInt(), (sun + 1f)) // Night Purple to Sunset
            else -> lerpColor(0xFFFF7043.toInt(), 0xFFFF9800.toInt(), sun) // Sunset to Noon
        }

        // 4. Calculate Continuous Surface (Glass)
        val surfaceAlpha = if (state.isDarkState) 0x66000000 else 0x66FFFFFF
        state.dominantSurfaceColor = (surfaceAlpha.toLong() and 0xFF000000L).toInt() or (state.dominantFoundationColor and 0x00FFFFFF)

        // 5. Calculate Continuous OnSurface (Text)
        state.dominantOnSurfaceColor = if (state.isDarkState) 0xFFF5F5F5.toInt() else 0xFF1A1A1A.toInt()
    }

    /**
     * Simple Integer ARGB color interpolation.
     */
    private fun lerpColor(from: Int, to: Int, fraction: Float): Int {
        val f = fraction.coerceIn(0f, 1f)
        val a1 = (from shr 24) and 0xff
        val r1 = (from shr 16) and 0xff
        val g1 = (from shr 8) and 0xff
        val b1 = from and 0xff

        val a2 = (to shr 24) and 0xff
        val r2 = (to shr 16) and 0xff
        val g2 = (to shr 8) and 0xff
        val b2 = to and 0xff

        return ((a1 + (a2 - a1) * f).toInt() shl 24) or
               ((r1 + (r2 - r1) * f).toInt() shl 16) or
               ((g1 + (g2 - g1) * f).toInt() shl 8) or
               ((b1 + (b2 - b1) * f).toInt())
    }
}
