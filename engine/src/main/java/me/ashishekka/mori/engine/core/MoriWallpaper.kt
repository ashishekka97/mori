package me.ashishekka.mori.engine.core

import me.ashishekka.mori.engine.renderer.EffectRenderer
import me.ashishekka.mori.engine.renderer.LayerManager

/**
 * A complete wallpaper definition.
 * Encapsulates the visual layers and the base environment configuration.
 * 
 * This is the precursor to the Biome DSL in Phase 6.
 */
class MoriWallpaper(
    val id: String,
    val layers: List<EffectRenderer>,
    val baseBackgroundColor: Int = 0xFF000000.toInt() // Solid Black foundation
) {
    companion object {
        /**
         * Creates the standard Phase 4/5 Smoke Test wallpaper.
         */
        fun createDebugWallpaper(): MoriWallpaper {
            return MoriWallpaper(
                id = "debug_pulse",
                layers = listOf(
                    // Note: We no longer use StaticFallbackRenderer as a layer here,
                    // because the Engine will handle the solid base color.
                    me.ashishekka.mori.engine.renderer.DebugPulseRenderer()
                ),
                baseBackgroundColor = 0xFF121212.toInt() // Opaque Dark Grey foundation
            )
        }
    }
}
