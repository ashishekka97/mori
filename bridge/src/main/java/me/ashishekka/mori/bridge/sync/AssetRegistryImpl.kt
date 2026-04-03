package me.ashishekka.mori.bridge.sync

import android.graphics.BitmapFactory
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.core.graphics.PathParser
import me.ashishekka.mori.engine.core.interfaces.AssetRegistry
import me.ashishekka.mori.engine.core.models.AssetType
import me.ashishekka.mori.engine.core.models.AtlasRegion
import java.io.InputStream
import java.nio.charset.Charset

/**
 * Implementation of [AssetRegistry] that lives in the :bridge module.
 * Bridges the gap between Android resources (app) and the platform-agnostic :engine.
 */
class AssetRegistryImpl : AssetRegistry {
    
    private val loadedAssets = mutableSetOf<Int>()
    private val atlas by lazy { BitmapTextureAtlas() }
    private val assetBounds = mutableMapOf<Int, AtlasRegion>()
    private val shaders = mutableMapOf<Int, Any>()
    private val paths = mutableMapOf<Int, Path>()

    override fun registerAsset(resId: Int, type: AssetType, stream: InputStream) {
        if (loadedAssets.contains(resId)) {
            // Already loaded, just close the stream and return.
            try { stream.close() } catch (e: Exception) {}
            return
        }

        when (type) {
            AssetType.BITMAP -> {
                val bitmap = BitmapFactory.decodeStream(stream)
                if (bitmap != null) {
                    val region = atlas.pack(bitmap)
                    if (region != null) {
                        assetBounds[resId] = region
                        loadedAssets.add(resId)
                    }
                    bitmap.recycle()
                }
            }
            AssetType.SHADER -> {
                val shaderString = stream.readBytes().toString(Charset.defaultCharset())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    try {
                        val shader = RuntimeShader(shaderString)
                        shaders[resId] = shader
                        loadedAssets.add(resId)
                    } catch (e: IllegalArgumentException) {
                        // Malformed shader string, do not mark as loaded
                    }
                } else {
                    // For older SDKs, we might not support AGSL, but we still track it as "loaded" 
                    // so we don't keep trying to load it. The canvas will just ignore it.
                    loadedAssets.add(resId)
                }
            }
            AssetType.PATH -> {
                val pathString = stream.readBytes().toString(Charset.defaultCharset()).trim()
                try {
                    val path = PathParser.createPathFromPathData(pathString)
                    paths[resId] = path
                    loadedAssets.add(resId)
                } catch (e: Exception) {
                    // Malformed path data, do not mark as loaded
                }
            }
            else -> {
                loadedAssets.add(resId)
            }
        }
        
        // CRITICAL: Close the stream after use.
        try { stream.close() } catch (e: Exception) {}
    }

    override fun getAtlasRegion(resId: Int): AtlasRegion {
        return assetBounds[resId] ?: AtlasRegion.EMPTY
    }

    override fun getAtlas(): Any? = atlas.getAtlasBitmap()
    
    override fun getShader(resId: Int): Any? = shaders[resId]

    override fun getStoredPath(resId: Int): Any? = paths[resId]

    override fun releaseAsset(resId: Int) {
        loadedAssets.remove(resId)
        assetBounds.remove(resId)
        shaders.remove(resId)
        paths.remove(resId)
        // Note: Removing from the atlas is not supported in this simple packer.
        // We assume assets are loaded once per biome session.
    }

    override fun clear() {
        loadedAssets.clear()
        assetBounds.clear()
        shaders.clear()
        paths.clear()
        atlas.clear()
    }

    override fun isReady(resId: Int): Boolean {
        return loadedAssets.contains(resId)
    }
}
