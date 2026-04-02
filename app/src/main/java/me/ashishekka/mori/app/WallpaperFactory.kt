package me.ashishekka.mori.app

import me.ashishekka.mori.biome.decoder.BiomeDecoder
import me.ashishekka.mori.biome.provider.AssetBiomeProvider
import me.ashishekka.mori.biome.provider.BiomeProvider
import me.ashishekka.mori.engine.core.MoriWallpaper
import me.ashishekka.mori.engine.core.RuleEvaluator
import me.ashishekka.mori.engine.core.interfaces.AssetRegistry
import me.ashishekka.mori.engine.renderer.DslEffectRenderer
import me.ashishekka.mori.engine.renderer.StaticFallbackRenderer

/**
 * Factory for creating [MoriWallpaper] instances.
 * This lives in the :app module because it needs access to all components
 * (:biome for decoding, :engine for rendering).
 */
class WallpaperFactory(
    private val provider: me.ashishekka.mori.biome.provider.BiomeProvider,
    private val evaluator: RuleEvaluator,
    private val assetRegistry: me.ashishekka.mori.engine.core.interfaces.AssetRegistry
) {
    fun createDebugPrismWallpaper(): MoriWallpaper {
        val biomeId = "prism_demo"
        val model = provider.getBiome(biomeId) ?: return MoriWallpaper.createDebugWallpaper()
        
        // Register Resources from the Biome Provider
        model.resources.forEach { res ->
            provider.openAsset(biomeId, res.path)?.let { stream ->
                assetRegistry.registerAsset(res.id, stream)
            }
        }

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
