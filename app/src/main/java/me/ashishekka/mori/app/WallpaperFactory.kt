package me.ashishekka.mori.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.ashishekka.mori.biome.decoder.BiomeDecoder
import me.ashishekka.mori.biome.models.BiomeModel
import me.ashishekka.mori.biome.provider.AssetBiomeProvider
import me.ashishekka.mori.biome.provider.BiomeProvider
import me.ashishekka.mori.engine.core.MoriWallpaper
import me.ashishekka.mori.engine.core.RuleEvaluator
import me.ashishekka.mori.engine.core.interfaces.AssetRegistry
import me.ashishekka.mori.engine.core.models.AssetType
import me.ashishekka.mori.engine.renderer.DslEffectRenderer
import me.ashishekka.mori.engine.renderer.EffectRenderer
import me.ashishekka.mori.engine.renderer.StaticFallbackRenderer

/**
 * Factory for creating [MoriWallpaper] instances.
 * This lives in the :app module because it needs access to all components
 * (:biome for decoding, :engine for rendering).
 */
class WallpaperFactory(
    private val provider: BiomeProvider,
    private val evaluator: RuleEvaluator,
    private val assetRegistry: AssetRegistry
) {
    suspend fun loadWallpaper(biomeId: String): MoriWallpaper = withContext(Dispatchers.IO) {
        // Clear before load to ensure a clean slate
        assetRegistry.clear()
        
        val model = provider.getBiome(biomeId) ?: return@withContext MoriWallpaper.createDebugWallpaper()
        
        // Register Resources from the Biome Provider
        model.resources.forEach { res ->
            val type = when (res.type.uppercase()) {
                "BITMAP" -> AssetType.BITMAP
                "SHADER" -> AssetType.SHADER
                "PATH" -> AssetType.PATH
                else -> AssetType.UNKNOWN
            }
            provider.openAsset(biomeId, res.path)?.let { stream ->
                assetRegistry.registerAsset(res.id, type, stream)
            }
        }

        val engineLayers = BiomeDecoder.compileToLayers(model)
        
        val renderers = mutableListOf<EffectRenderer>()
        renderers.add(StaticFallbackRenderer())
        
        engineLayers.forEach { layer ->
            renderers.add(DslEffectRenderer(layer, evaluator))
        }

        MoriWallpaper(
            id = biomeId,
            layers = renderers
        )
    }

    fun createDebugPrismWallpaper(): MoriWallpaper {
        val biomeId = "childhood_canvas"
        val model = provider.getBiome(biomeId) ?: return MoriWallpaper.createDebugWallpaper()
        
        // Register Resources from the Biome Provider
        model.resources.forEach { res ->
            val type = when (res.type.uppercase()) {
                "BITMAP" -> AssetType.BITMAP
                "SHADER" -> AssetType.SHADER
                "PATH" -> AssetType.PATH
                else -> AssetType.UNKNOWN
            }
            provider.openAsset(biomeId, res.path)?.let { stream ->
                assetRegistry.registerAsset(res.id, type, stream)
            }
        }

        val engineLayers = BiomeDecoder.compileToLayers(model)
        
        val renderers = mutableListOf<EffectRenderer>()
        renderers.add(StaticFallbackRenderer())
        
        engineLayers.forEach { layer ->
            renderers.add(DslEffectRenderer(layer, evaluator))
        }

        return MoriWallpaper(
            id = "childhood_canvas",
            layers = renderers
        )
    }
}
