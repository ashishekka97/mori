package me.ashishekka.mori.bridge.sync

import me.ashishekka.mori.engine.core.interfaces.AssetRegistry

/**
 * Implementation of [AssetRegistry] that lives in the :bridge module.
 * Bridges the gap between Android resources (app) and the platform-agnostic :engine.
 */
class AssetRegistryImpl : AssetRegistry {
    
    private val loadedAssets = mutableSetOf<Int>()

    override fun registerAsset(resId: Int, stream: java.io.InputStream) {
        // Phase 7.1.1: This will decode the stream into a Bitmap and pack it into the TextureAtlas.
        // For now, we just track the registration.
        loadedAssets.add(resId)
        
        // CRITICAL: Close the stream after use.
        try { stream.close() } catch (e: Exception) {}
    }

    override fun releaseAsset(resId: Int) {
        loadedAssets.remove(resId)
    }

    override fun clear() {
        loadedAssets.clear()
    }

    override fun isReady(resId: Int): Boolean {
        return loadedAssets.contains(resId)
    }
}
