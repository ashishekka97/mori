package me.ashishekka.mori.bridge.sync

import android.graphics.BitmapFactory
import android.graphics.Rect
import me.ashishekka.mori.engine.core.interfaces.AssetRegistry
import me.ashishekka.mori.engine.core.models.AssetType
import me.ashishekka.mori.engine.core.models.AtlasRegion
import java.io.InputStream

/**
 * Implementation of [AssetRegistry] that lives in the :bridge module.
 * Bridges the gap between Android resources (app) and the platform-agnostic :engine.
 */
class AssetRegistryImpl : AssetRegistry {
    
    private val loadedAssets = mutableSetOf<Int>()
    private val atlas by lazy { BitmapTextureAtlas() }
    private val assetBounds = mutableMapOf<Int, AtlasRegion>()

    override fun registerAsset(resId: Int, type: AssetType, stream: InputStream) {
        if (loadedAssets.contains(resId)) {
            // Already loaded, just close the stream and return.
            try { stream.close() } catch (e: Exception) {}
            return
        }

        if (type == AssetType.BITMAP) {
            val bitmap = BitmapFactory.decodeStream(stream)
            if (bitmap != null) {
                val region = atlas.pack(bitmap)
                if (region != null) {
                    assetBounds[resId] = region
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

    override fun getAtlasRegion(resId: Int): AtlasRegion {
        return assetBounds[resId] ?: AtlasRegion.EMPTY
    }

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
