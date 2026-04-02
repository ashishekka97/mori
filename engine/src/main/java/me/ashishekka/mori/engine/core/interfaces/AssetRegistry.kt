package me.ashishekka.mori.engine.core.interfaces
import me.ashishekka.mori.engine.core.models.AssetType
import java.io.InputStream

/**
 * Contract for a system that manages high-performance visual assets (Bitmaps/Shaders).
 */
interface AssetRegistry {
    /**
     * Pre-loads an asset into GPU memory (e.g., into the Texture Atlas).
     * @param resId The internal ID used by the Engine layers.
     * @param type The type of asset (BITMAP or SHADER).
     * @param stream The source data for the asset.
     */
    fun registerAsset(resId: Int, type: AssetType, stream: InputStream)

    /**
     * Returns the base width of the asset in pixels.
     */
    fun getAssetWidth(resId: Int): Int

    /**
     * Returns the base height of the asset in pixels.
     */
    fun getAssetHeight(resId: Int): Int

    /**
     * Returns the x-coordinate of the asset within the texture atlas.
     */
    fun getAssetLeft(resId: Int): Int

    /**
     * Returns the y-coordinate of the asset within the texture atlas.
     */
    fun getAssetTop(resId: Int): Int

    /**
     * Returns the platform-specific texture atlas object (e.g., Bitmap).
     */
    fun getAtlas(): Any?

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
