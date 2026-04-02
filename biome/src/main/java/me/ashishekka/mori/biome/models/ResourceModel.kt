package me.ashishekka.mori.biome.models

import kotlinx.serialization.Serializable

/**
 * Defines a visual resource (Bitmap or AGSL Shader) for use in a Biome.
 */
@Serializable
data class ResourceModel(
    val id: Int,
    val path: String,
    val type: String // "BITMAP", "SHADER"
)
