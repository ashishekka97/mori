package me.ashishekka.mori.engine.core

import me.ashishekka.mori.engine.core.interfaces.EngineTicker
import me.ashishekka.mori.engine.core.interfaces.RenderSurface
import me.ashishekka.mori.engine.renderer.EffectRenderer
import me.ashishekka.mori.engine.renderer.StaticFallbackRenderer

/**
 * The core rendering engine for Mori.
 * This class is a platform-agnostic orchestrator that delegates timing to [EngineTicker]
 * and drawing to [RenderSurface].
 */
class MoriEngine(
    private val ticker: EngineTicker,
    private val renderSurface: RenderSurface,
    private val fallbackRenderer: EffectRenderer = StaticFallbackRenderer(0xFF121212.toInt())
) {

    // FPS Control
    var targetFps: Int = 60
        set(value) {
            field = value.coerceIn(1, 120) // Sanity check
            frameIntervalNanos = 1_000_000_000L / field
        }

    private var lastFrameTimeNanos: Long = 0L
    private var frameIntervalNanos: Long = 1_000_000_000L / targetFps

    init {
        ticker.setOnTickCallback { frameTimeNanos ->
            val delta = frameTimeNanos - lastFrameTimeNanos
            if (delta >= frameIntervalNanos) {
                onDrawFrame()
                lastFrameTimeNanos = frameTimeNanos
            }
        }
    }

    /**
     * Starts the engine and begins rendering.
     */
    fun start() {
        lastFrameTimeNanos = 0L // Reset to trigger immediate draw
        ticker.start()
    }

    /**
     * Stops the engine.
     */
    fun stop() {
        ticker.stop()
    }

    /**
     * Toggles whether the engine continuously renders frames (e.g., 60 FPS) 
     * or only renders when a frame is explicitly requested.
     */
    fun setContinuousRendering(enabled: Boolean) {
        ticker.setContinuous(enabled)
    }

    /**
     * Requests a single frame to be rendered.
     */
    fun requestFrame() {
        ticker.requestTick()
    }

    /**
     * Triggers the rendering of a single frame.
     */
    fun onDrawFrame() {
        val canvas = renderSurface.lockCanvas()

        canvas?.let {
            try {
                // In the future: layerManager.updateAndDraw(it)
                it.drawColor(0xFF121212.toInt())
            } catch (e: Throwable) {
                // Failsafe: if the complex render loop fails, draw the fallback
                fallbackRenderer.updateAndDraw(it)
            } finally {
                renderSurface.unlockCanvasAndPost(it)
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
