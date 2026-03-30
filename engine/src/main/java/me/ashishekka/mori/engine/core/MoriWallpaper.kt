package me.ashishekka.mori.engine.core

import me.ashishekka.mori.engine.core.util.ColorUtils
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
     * Uses Weighted OKLab Blending to ensure perceptual vibrancy.
     */
    fun synthesizePalette(state: MoriEngineState) {
        var finalAccent: Int? = null
        var accentWeightSum = 0f

        var finalFoundation: Int? = null
        var foundationWeightSum = 0f

        var finalSurface: Int? = null
        var surfaceWeightSum = 0f

        var finalOnSurface: Int? = null
        var onSurfaceWeightSum = 0f

        // ZERO-ALLOCATION Loop
        var i = 0
        val size = layers.size
        while (i < size) {
            val contrib = layers[i].getPaletteContribution()
            if (contrib != null) {
                // Weighted OKLab Accumulation
                if (contrib.accent != null) {
                    finalAccent = if (finalAccent == null) contrib.accent 
                                 else ColorUtils.lerpColorOklab(finalAccent, contrib.accent, contrib.accentWeight / (accentWeightSum + contrib.accentWeight))
                    accentWeightSum += contrib.accentWeight
                }
                if (contrib.foundation != null) {
                    finalFoundation = if (finalFoundation == null) contrib.foundation
                                     else ColorUtils.lerpColorOklab(finalFoundation, contrib.foundation, contrib.foundationWeight / (foundationWeightSum + contrib.foundationWeight))
                    foundationWeightSum += contrib.foundationWeight
                }
                if (contrib.surface != null) {
                    finalSurface = if (finalSurface == null) contrib.surface
                                  else ColorUtils.lerpColorOklab(finalSurface, contrib.surface, contrib.surfaceWeight / (surfaceWeightSum + contrib.surfaceWeight))
                    surfaceWeightSum += contrib.surfaceWeight
                }
                if (contrib.onSurface != null) {
                    finalOnSurface = if (finalOnSurface == null) contrib.onSurface
                                    else ColorUtils.lerpColorOklab(finalOnSurface, contrib.onSurface, contrib.onSurfaceWeight / (onSurfaceWeightSum + contrib.onSurfaceWeight))
                    onSurfaceWeightSum += contrib.onSurfaceWeight
                }
            }
            i++
        }

        // Apply synthesized or fallback colors
        state.dominantFoundationColor = finalFoundation ?: 0xFF121212.toInt()
        state.dominantAccentColor = finalAccent ?: 0xFF9575CD.toInt()
        state.dominantSurfaceColor = finalSurface ?: deriveSurface(state)
        state.dominantOnSurfaceColor = finalOnSurface ?: deriveOnSurface(state)
        
        val sunAltitude = state.getFieldValue(MoriEngineStateIndices.FACT_SUN_ALTITUDE)
        state.isDarkState = sunAltitude <= 0.2f
    }

    /** Derives a glass-like surface color if not explicitly provided. */
    private fun deriveSurface(state: MoriEngineState): Int {
        val sunAltitude = state.getFieldValue(MoriEngineStateIndices.FACT_SUN_ALTITUDE)
        val isDark = sunAltitude <= 0.2f
        val surfaceAlpha = if (isDark) 0x4D000000 else 0x4DFFFFFF
        return (surfaceAlpha.toLong() and 0xFF000000L).toInt() or (state.dominantFoundationColor and 0x00FFFFFF)
    }

    /** Derives a high-contrast text/icon color if not explicitly provided. */
    private fun deriveOnSurface(state: MoriEngineState): Int {
        val sunAltitude = state.getFieldValue(MoriEngineStateIndices.FACT_SUN_ALTITUDE)
        val isDark = sunAltitude <= 0.2f
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
