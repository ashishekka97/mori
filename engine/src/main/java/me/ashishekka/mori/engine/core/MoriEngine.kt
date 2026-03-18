package me.ashishekka.mori.engine.core

import android.service.wallpaper.WallpaperService
import android.view.Choreographer
import android.view.SurfaceHolder
import me.ashishekka.mori.engine.renderer.EffectRenderer
import me.ashishekka.mori.engine.renderer.StaticFallbackRenderer

/**
 * The core rendering engine for Mori.
 * This class is responsible for the actual drawing on the [SurfaceHolder]'s canvas.
 * It is managed by the [WallpaperService.Engine] lifecycle.
 *
 * Implements [Choreographer.FrameCallback] to drive a 60FPS (or display native) rendering loop.
 */
class MoriEngine(
    private val serviceEngine: WallpaperService.Engine,
    private val choreographer: Choreographer = Choreographer.getInstance(),
    private val fallbackRenderer: EffectRenderer = StaticFallbackRenderer(0xFF121212.toInt())
) : Choreographer.FrameCallback {
    private var isRunning = false

    // FPS Control
    var targetFps: Int = 60
        set(value) {
            field = value.coerceIn(1, 120) // Sanity check
            frameIntervalNanos = 1_000_000_000L / field
        }

    private var lastFrameTimeNanos: Long = 0L
    private var frameIntervalNanos: Long = 1_000_000_000L / targetFps

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
            lastFrameTimeNanos = 0L // Reset to trigger immediate draw
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

        val delta = frameTimeNanos - lastFrameTimeNanos
        if (delta >= frameIntervalNanos) {
            onDrawFrame()
            lastFrameTimeNanos = frameTimeNanos
        }

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
            try {
                // In the future: layerManager.updateAndDraw(it)
                it.drawColor(0xFF121212.toInt())
            } catch (e: Throwable) {
                // Failsafe: if the complex render loop fails, draw the fallback
                fallbackRenderer.updateAndDraw(it)
            } finally {
                holder.unlockCanvasAndPost(it)
            }
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
