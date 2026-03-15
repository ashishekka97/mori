package me.ashishekka.mori.engine

import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import androidx.core.graphics.toColorInt

/**
 * The core rendering engine for Mori.
 * This class is responsible for the actual drawing on the [SurfaceHolder]'s canvas.
 * It is managed by the [WallpaperService.Engine] lifecycle.
 */
class MoriEngine(
    private val serviceEngine: WallpaperService.Engine
) {

    /**
     * Called when the engine is first created.
     * Pre-allocate your paints, paths, and bitmaps here. No 'new' or allocations allowed after this!
     */
    fun onCreate(surfaceHolder: SurfaceHolder) {
        // Initial setup
    }

    /**
     * Triggers the rendering of a single frame.
     * In the future, this will be driven by the Choreographer loop.
     */
    fun onDrawFrame() {
        val holder = serviceEngine.surfaceHolder
        val canvas = try {
            holder.lockCanvas()
        } catch (e: Exception) {
            null
        }

        canvas?.let {
            it.drawColor("#121212".toColorInt()) // Mori Dark Grey
            holder.unlockCanvasAndPost(it)
        }
    }

    /**
     * Called when the engine is destroyed.
     * Clean up resources and cancel any pending work.
     */
    fun onDestroy() {
        // Resource cleanup
    }
}
