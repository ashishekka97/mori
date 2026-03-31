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
    /** Map of property names to their DSL expressions. */
    val expressions: Map<String, String> = emptyMap()
)
