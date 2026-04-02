package me.ashishekka.mori.biome.provider

import me.ashishekka.mori.biome.models.BiomeModel
import java.io.InputStream

/**
 * Contract for a system that provides complete Biomes (DSL + Bundled Assets).
 * This ensures the :biome module remains the source of truth for all visual resources.
 */
interface BiomeProvider {
    /**
     * Loads the declarative JSON model for a biome.
     */
    fun getBiome(biomeId: String): BiomeModel?

    /**
     * Opens a stream for a bundled asset (Bitmap/Shader) associated with a biome.
     * @param biomeId The ID of the biome the asset belongs to.
     * @param assetPath The relative path within the biome's asset bundle.
     */
    fun openAsset(biomeId: String, assetPath: String): InputStream?
}
