package me.ashishekka.mori.engine.core.interfaces

/**
 * Contract for a system that manages high-performance visual assets (Bitmaps/Shaders).
 */
interface AssetRegistry {
    /**
     * Pre-loads an asset into GPU memory (e.g., into the Texture Atlas).
     * @param resId The internal ID used by the Engine layers.
     * @param stream The source data for the asset (Bitmap or Shader source).
     */
    fun registerAsset(resId: Int, stream: java.io.InputStream)

    /**
     * Releases an asset from memory.
     */
    fun releaseAsset(resId: Int)

    /**
     * Clears all assets.
     */
    fun clear()

    /**
     * Returns true if the asset is loaded and ready to be drawn.
     */
    fun isReady(resId: Int): Boolean
}
