package me.ashishekka.mori.engine.core

import android.service.wallpaper.WallpaperService
import android.view.Choreographer
import android.view.SurfaceHolder

/**
 * The core rendering engine for Mori.
 * This class is responsible for the actual drawing on the [SurfaceHolder]'s canvas.
 * It is managed by the [WallpaperService.Engine] lifecycle.
 *
 * Implements [Choreographer.FrameCallback] to drive a 60FPS (or display native) rendering loop.
 */
class MoriEngine(
    private val serviceEngine: WallpaperService.Engine,
    private val choreographer: Choreographer = Choreographer.getInstance()
) : Choreographer.FrameCallback {
    private var isRunning = false

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
     * Starts the rendering loop.
     */
    fun start() {
        if (!isRunning) {
            isRunning = true
            choreographer.postFrameCallback(this)
        }
    }

    /**
     * Stops the rendering loop.
     */
    fun stop() {
        isRunning = false
        choreographer.removeFrameCallback(this)
    }

    /**
     * Choreographer callback triggered on every VSYNC.
     */
    override fun doFrame(frameTimeNanos: Long) {
        if (!isRunning) return

        onDrawFrame()

        // Schedule the next frame
        choreographer.postFrameCallback(this)
    }

    /**
     * Triggers the rendering of a single frame.
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
        stop()
    }
}
