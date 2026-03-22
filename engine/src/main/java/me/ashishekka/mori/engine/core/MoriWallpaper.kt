package me.ashishekka.mori.engine.core

import me.ashishekka.mori.engine.renderer.DebugPulseRenderer
import me.ashishekka.mori.engine.renderer.EffectRenderer
import me.ashishekka.mori.engine.renderer.RendererPalette
import me.ashishekka.mori.engine.renderer.StaticFallbackRenderer

/**
 * A complete wallpaper definition.
 * Encapsulates visual layers and the logic to synthesize their colors into a UI theme.
 */
class MoriWallpaper(
    val id: String,
    val layers: List<EffectRenderer>
) {
    /**
     * Aggregates color contributions from all layers to produce a unified UI theme.
     */
    fun synthesizePalette(state: MoriEngineState) {
        var finalAccent: Int? = null
        var finalFoundation: Int? = null
        var finalSurface: Int? = null
        var finalOnSurface: Int? = null

        // Collect all palette contributions from layers, in order.
        // The first layer to provide a non-null color wins.
        layers.forEach { layer ->
            val contrib = layer.getPaletteContribution()
            if (finalAccent == null) finalAccent = contrib?.accent
            if (finalFoundation == null) finalFoundation = contrib?.foundation
            if (finalSurface == null) finalSurface = contrib?.surface
            if (finalOnSurface == null) finalOnSurface = contrib?.onSurface
        }

        // Apply synthesized or fallback colors to the global state.
        state.dominantFoundationColor = finalFoundation ?: 0xFF121212.toInt()
        state.dominantAccentColor = finalAccent ?: 0xFF9575CD.toInt()
        
        // TRUE SYNTHESIS: Prioritize the contributed surface/onSurface colors.
        state.dominantSurfaceColor = finalSurface ?: deriveSurface(state)
        state.dominantOnSurfaceColor = finalOnSurface ?: deriveOnSurface(state)
        
        // Ensure isDarkState is consistent with the final foundation color.
        state.isDarkState = state.chronosSunAltitude <= 0.2f
    }

    /** Derives a glass-like surface color if not explicitly provided. */
    private fun deriveSurface(state: MoriEngineState): Int {
        val isDark = state.chronosSunAltitude <= 0.2f
        val surfaceAlpha = if (isDark) 0x4D000000 else 0x4DFFFFFF
        return (surfaceAlpha.toLong() and 0xFF000000L).toInt() or (state.dominantFoundationColor and 0x00FFFFFF)
    }

    /** Derives a high-contrast text/icon color if not explicitly provided. */
    private fun deriveOnSurface(state: MoriEngineState): Int {
        val isDark = state.chronosSunAltitude <= 0.2f
        return if (isDark) 0xFFFFFFFF.toInt() else 0xFF000000.toInt()
    }

    companion object {
        fun createDebugWallpaper(): MoriWallpaper {
            return MoriWallpaper(
                id = "debug_aurora",
                layers = listOf(
                    StaticFallbackRenderer(), 
                    DebugPulseRenderer()
                )
            )
        }
    }
}
