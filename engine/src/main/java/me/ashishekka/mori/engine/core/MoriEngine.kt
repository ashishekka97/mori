package me.ashishekka.mori.engine.core

import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder

/**
 * The core rendering engine for Mori.
 * This class is responsible for the actual drawing on the [SurfaceHolder]'s canvas.
 * It is managed by the [WallpaperService.Engine] lifecycle.
 */
class MoriEngine(
    private val serviceEngine: WallpaperService.Engine
) {

    // Pre-allocated color constant (Mori Dark Grey)
    private val defaultBackgroundColor: Int = 0xFF121212.toInt()

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
            it.drawColor(defaultBackgroundColor)
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
