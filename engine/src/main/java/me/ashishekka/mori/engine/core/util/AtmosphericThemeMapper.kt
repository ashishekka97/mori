package me.ashishekka.mori.engine.core.util

import me.ashishekka.mori.engine.core.MoriEngineState

/**
 * A dedicated utility to map atmospheric world signals to UI theme tokens.
 * This class serves as the bridge between the Simulation and the Pulse Design System.
 * 
 * In Phase 6, this logic will be replaced by a declarative Biome DSL.
 */
object AtmosphericThemeMapper {

    /**
     * Updates the engine state's dominant palette based on the current world metrics.
     */
    fun updatePalette(state: MoriEngineState) {
        val isDay = state.chronosSunAltitude > 0
        state.isDarkState = !isDay

        if (isDay) {
            state.dominantAccentColor = 0xFFFFB74D.toInt() // Day Accent (Amber)
            state.dominantSurfaceColor = 0x44FFFFFF.toInt() // Day Surface
            state.dominantOnSurfaceColor = 0xFF333333.toInt() // Day OnSurface (Dark Grey)
        } else {
            state.dominantAccentColor = 0xFF9575CD.toInt() // Night Accent (Purple)
            state.dominantSurfaceColor = 0x44000000.toInt() // Night Surface
            state.dominantOnSurfaceColor = 0xFFF5F5F5.toInt() // Night OnSurface (Off White)
        }
    }
}
