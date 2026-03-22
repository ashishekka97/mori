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
            state.dominantFoundationColor = 0xFFF5F5F5.toInt() // Day Foundation (Light)
            state.dominantAccentColor = 0xFFFFB74D.toInt() // Day Accent (Amber)
            state.dominantSurfaceColor = 0x44FFFFFF.toInt() // Day Surface (Translucent White)
            state.dominantOnSurfaceColor = 0xFF333333.toInt() // Day OnSurface (Dark Grey)
        } else {
            state.dominantFoundationColor = 0xFF121212.toInt() // Night Foundation (Dark)
            state.dominantAccentColor = 0xFF9575CD.toInt() // Night Accent (Purple)
            state.dominantSurfaceColor = 0x44000000.toInt() // Night Surface (Translucent Black)
            state.dominantOnSurfaceColor = 0xFFF5F5F5.toInt() // Night OnSurface (Off White)
        }
    }
}
