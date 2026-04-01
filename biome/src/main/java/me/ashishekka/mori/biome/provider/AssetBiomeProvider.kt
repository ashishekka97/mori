package me.ashishekka.mori.biome.provider

import android.content.Context
import me.ashishekka.mori.biome.decoder.BiomeDecoder
import me.ashishekka.mori.biome.models.BiomeModel

/**
 * Provider for Biome definitions stored in the Android Assets folder.
 */
class AssetBiomeProvider(private val context: Context) {

    /**
     * Loads a biome by its ID from assets/biomes/{id}.json.
     * Returns null if the file is missing or malformed.
     */
    fun getBiome(biomeId: String): BiomeModel? {
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
}
