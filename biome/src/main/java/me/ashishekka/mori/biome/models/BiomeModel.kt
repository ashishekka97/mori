package me.ashishekka.mori.biome.models

import kotlinx.serialization.Serializable

/**
 * Declarative model for a Mori Biome.
 * Represents the JSON structure of a wallpaper definition.
 */
@Serializable
data class BiomeModel(
    val id: String,
    val name: String,
    val description: String? = null,
    val layers: List<LayerModel> = emptyList()
)
