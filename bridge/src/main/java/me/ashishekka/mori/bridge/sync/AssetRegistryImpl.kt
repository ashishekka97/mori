package me.ashishekka.mori.bridge.sync

import android.graphics.BitmapFactory
import android.graphics.Rect
import me.ashishekka.mori.engine.core.interfaces.AssetRegistry
import me.ashishekka.mori.engine.core.models.AssetType
import java.io.InputStream

/**
 * Implementation of [AssetRegistry] that lives in the :bridge module.
 * Bridges the gap between Android resources (app) and the platform-agnostic :engine.
 */
class AssetRegistryImpl : AssetRegistry {
    
    private val loadedAssets = mutableSetOf<Int>()
    private val atlas = BitmapTextureAtlas()
    private val assetBounds = mutableMapOf<Int, Rect>()

    override fun registerAsset(resId: Int, type: AssetType, stream: InputStream) {
        if (type == AssetType.BITMAP) {
            val bitmap = BitmapFactory.decodeStream(stream)
            if (bitmap != null) {
                val rect = atlas.pack(bitmap)
                if (rect != null) {
                    assetBounds[resId] = rect
                    loadedAssets.add(resId)
                }
                bitmap.recycle()
            }
        } else {
            // For other asset types (e.g. SHADERS), we just track registration for now.
            loadedAssets.add(resId)
        }
        
        // CRITICAL: Close the stream after use.
        try { stream.close() } catch (e: Exception) {}
    }

    override fun getAssetWidth(resId: Int): Int = assetBounds[resId]?.width() ?: 0

    override fun getAssetHeight(resId: Int): Int = assetBounds[resId]?.height() ?: 0

    override fun getAssetLeft(resId: Int): Int = assetBounds[resId]?.left ?: 0

    override fun getAssetTop(resId: Int): Int = assetBounds[resId]?.top ?: 0

    override fun getAtlas(): Any? = atlas.getAtlasBitmap()

    override fun releaseAsset(resId: Int) {
        loadedAssets.remove(resId)
        assetBounds.remove(resId)
        // Note: Removing from the atlas is not supported in this simple packer.
        // We assume assets are loaded once per biome session.
    }

    override fun clear() {
        loadedAssets.clear()
        assetBounds.clear()
        atlas.clear()
    }

    override fun isReady(resId: Int): Boolean {
        return loadedAssets.contains(resId)
    }
}
