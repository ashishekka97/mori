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
     * Uses a Granular Weighted Precedence strategy.
     */
    fun synthesizePalette(state: MoriEngineState) {
        var finalAccent: Int? = null
        var maxAccentWeight = -1.0f

        var finalFoundation: Int? = null
        var maxFoundationWeight = -1.0f

        var finalSurface: Int? = null
        var maxSurfaceWeight = -1.0f

        var finalOnSurface: Int? = null
        var maxOnSurfaceWeight = -1.0f

        // ZERO-ALLOCATION Loop
        var i = 0
        val size = layers.size
        while (i < size) {
            val contrib = layers[i].getPaletteContribution()
            if (contrib != null) {
                
                if (contrib.accent != null && contrib.accentWeight >= maxAccentWeight) {
                    finalAccent = contrib.accent
                    maxAccentWeight = contrib.accentWeight
                }
                if (contrib.foundation != null && contrib.foundationWeight >= maxFoundationWeight) {
                    finalFoundation = contrib.foundation
                    maxFoundationWeight = contrib.foundationWeight
                }
                if (contrib.surface != null && contrib.surfaceWeight >= maxSurfaceWeight) {
                    finalSurface = contrib.surface
                    maxSurfaceWeight = contrib.surfaceWeight
                }
                if (contrib.onSurface != null && contrib.onSurfaceWeight >= maxOnSurfaceWeight) {
                    finalOnSurface = contrib.onSurface
                    maxOnSurfaceWeight = contrib.onSurfaceWeight
                }
            }
            i++
        }

        // Apply synthesized or fallback colors
        state.dominantFoundationColor = finalFoundation ?: 0xFF121212.toInt()
        state.dominantAccentColor = finalAccent ?: 0xFF9575CD.toInt()
        state.dominantSurfaceColor = finalSurface ?: deriveSurface(state)
        state.dominantOnSurfaceColor = finalOnSurface ?: deriveOnSurface(state)
        
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
