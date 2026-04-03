package me.ashishekka.mori.engine.core.models

/**
 * Represents a specific region (slice) within a Texture Atlas.
 * Used for "unpacking" a single asset from the larger combined texture.
 */
data class AtlasRegion(
    val left: Int,
    val top: Int,
    val width: Int,
    val height: Int
) {
    companion object {
        val EMPTY = AtlasRegion(0, 0, 0, 0)
    }
}
