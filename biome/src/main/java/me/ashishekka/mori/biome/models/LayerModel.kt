package me.ashishekka.mori.biome.models

import kotlinx.serialization.Serializable

/**
 * Declarative model for a single rendering layer.
 * Maps property names to math expressions (e.g., "x": "fact[2] * 100").
 */
@Serializable
data class LayerModel(
    val id: Int,
    val type: String, // e.g., "CIRCLE", "RECT", "PATH"
    val zOrder: Int = 0,
    /** Optional reference to a resource (Bitmap or Shader) in [BiomeModel.resources]. */
    val resId: Int? = null,
    /** Optional reference to a resource (Path) to use as a mask. */
    val maskId: Int? = null,
    /** Map of property names to their DSL expressions. */
    val expressions: Map<String, String> = emptyMap()
)
