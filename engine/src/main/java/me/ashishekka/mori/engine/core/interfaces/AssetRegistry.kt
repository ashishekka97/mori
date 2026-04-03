package me.ashishekka.mori.engine.core.interfaces
import me.ashishekka.mori.engine.core.models.AssetType
import me.ashishekka.mori.engine.core.models.AtlasRegion
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
     * Returns the [AtlasRegion] for a given [resId].
     * If the asset is not registered, returns [AtlasRegion.EMPTY].
     */
    fun getAtlasRegion(resId: Int): AtlasRegion

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
     * Returns the platform-specific compiled shader object (e.g., RuntimeShader).
     */
    fun getShader(resId: Int): Any?

    /**
     * Returns true if the asset is loaded and ready to be drawn.
     */
    fun isReady(resId: Int): Boolean
}
