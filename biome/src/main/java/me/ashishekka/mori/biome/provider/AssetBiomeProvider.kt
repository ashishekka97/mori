package me.ashishekka.mori.biome.provider

import android.content.Context
import me.ashishekka.mori.biome.decoder.BiomeDecoder
import me.ashishekka.mori.biome.models.BiomeModel
import java.io.InputStream

/**
 * Provider for Biome definitions stored in the Android Assets folder.
 */
class AssetBiomeProvider(private val context: Context) : BiomeProvider {

    /**
     * Loads a biome by its ID from assets/biomes/{id}.json.
     * Returns null if the file is missing or malformed.
     */
    override fun getBiome(biomeId: String): BiomeModel? {
        return try {
            val jsonString = context.assets.open("biomes/$biomeId.json")
                .bufferedReader()
                .use { it.readText() }
            
            val model = BiomeDecoder.decode(jsonString)
            if (model == null) {
                android.util.Log.e("Mori", "Failed to decode biome JSON for: $biomeId")
            }
            model
        } catch (e: Exception) {
            android.util.Log.e("Mori", "Error opening biome asset: $biomeId", e)
            null
        }
    }

    /**
     * Opens an asset stream for a biome from assets/biomes/{biomeId}/assets/{assetPath}.
     */
    override fun openAsset(biomeId: String, assetPath: String): InputStream? {
        return try {
            // Assets are bundled in a subfolder named after the biome ID
            context.assets.open("biomes/$biomeId/$assetPath")
        } catch (e: Exception) {
            android.util.Log.e("Mori", "Error opening bundled asset: $biomeId/$assetPath", e)
            null
        }
    }
}
