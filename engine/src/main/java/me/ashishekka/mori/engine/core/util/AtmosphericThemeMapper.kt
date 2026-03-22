package me.ashishekka.mori.engine.core.util

import me.ashishekka.mori.engine.core.MoriEngineState

/**
 * A dedicated utility to map atmospheric world signals to UI theme tokens.
 */
object AtmosphericThemeMapper {

    fun updatePalette(state: MoriEngineState) {
        val isDay = state.chronosSunAltitude > 0
        state.isDarkState = !isDay

        if (isDay) {
            // VIBRANT DAY: Crisp White & Amber
            state.dominantFoundationColor = 0xFFFFFFFF.toInt() 
            state.dominantAccentColor = 0xFFFF9800.toInt() // Vibrant Orange/Amber
            state.dominantSurfaceColor = 0x66FFFFFF.toInt() // More visible glass
            state.dominantOnSurfaceColor = 0xFF1A1A1A.toInt() // High-contrast black
        } else {
            // VIBRANT NIGHT: Deep Navy & Violet
            state.dominantFoundationColor = 0xFF0D1117.toInt() // GitHub Dark foundation
            state.dominantAccentColor = 0xFFBB86FC.toInt() // Vibrant Light Purple
            state.dominantSurfaceColor = 0x66000000.toInt() // More visible glass
            state.dominantOnSurfaceColor = 0xFFF5F5F5.toInt() // High-contrast white
        }
    }
}
