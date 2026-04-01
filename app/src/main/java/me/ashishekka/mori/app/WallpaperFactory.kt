package me.ashishekka.mori.app

import me.ashishekka.mori.biome.decoder.BiomeDecoder
import me.ashishekka.mori.biome.provider.AssetBiomeProvider
import me.ashishekka.mori.engine.core.MoriWallpaper
import me.ashishekka.mori.engine.core.RuleEvaluator
import me.ashishekka.mori.engine.renderer.DslEffectRenderer
import me.ashishekka.mori.engine.renderer.StaticFallbackRenderer

/**
 * Factory for creating [MoriWallpaper] instances.
 * This lives in the :app module because it needs access to all components
 * (:biome for decoding, :engine for rendering).
 */
class WallpaperFactory(
    private val provider: AssetBiomeProvider,
    private val evaluator: RuleEvaluator
) {
    fun createDebugPrismWallpaper(): MoriWallpaper {
        val model = provider.getBiome("prism_demo")
        val engineLayers = BiomeDecoder.compileToLayers(model)
        
        val renderers = mutableListOf<me.ashishekka.mori.engine.renderer.EffectRenderer>()
        renderers.add(StaticFallbackRenderer())
        
        engineLayers.forEach { layer ->
            renderers.add(DslEffectRenderer(layer, evaluator))
        }

        return MoriWallpaper(
            id = "prism_demo",
            layers = renderers
        )
    }
}
