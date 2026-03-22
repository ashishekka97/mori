package me.ashishekka.mori.engine.core

import me.ashishekka.mori.engine.core.interfaces.EngineTicker
import me.ashishekka.mori.engine.core.interfaces.RenderSurface
import me.ashishekka.mori.engine.core.models.ScaleMode
import me.ashishekka.mori.engine.core.util.AtmosphericThemeMapper
import me.ashishekka.mori.engine.renderer.EffectRenderer
import me.ashishekka.mori.engine.renderer.LayerManager
import me.ashishekka.mori.engine.renderer.StaticFallbackRenderer
import kotlin.math.max
import kotlin.math.min

/**
 * The core rendering engine for Mori.
 * This class is a platform-agnostic orchestrator that delegates timing to [EngineTicker]
 * and drawing to [RenderSurface].
 */
class MoriEngine(
    private val ticker: EngineTicker,
    private val renderSurface: RenderSurface,
    private val layerManager: LayerManager,
    private val fallbackRenderer: EffectRenderer = StaticFallbackRenderer(0xFF121212.toInt())
) {

    private var isRunning = false
    val state = MoriEngineState()

    // Geometric Configuration
    var targetScaleMode: ScaleMode = ScaleMode.FIT

    // FPS Control
    var targetFps: Int = 60
        set(value) {
            field = value.coerceIn(1, 120) // Sanity check
            frameIntervalNanos = 1_000_000_000L / field
        }

    private var frameIntervalNanos: Long = 1_000_000_000L / 60
    private var lastFrameTimeNanos: Long = 0L

    init {
        ticker.setOnTickCallback { frameTimeNanos ->
            if (!isRunning) return@setOnTickCallback
            val delta = frameTimeNanos - lastFrameTimeNanos
            if (delta >= frameIntervalNanos) {
                onDrawFrame(frameTimeNanos)
                lastFrameTimeNanos = frameTimeNanos
            }
        }
    }

    /**
     * Starts the rendering loop.
     */
    fun start() {
        if (isRunning) return
        isRunning = true
        ticker.start()
    }

    /**
     * Stops the rendering loop.
     */
    fun stop() {
        if (!isRunning) return
        isRunning = false
        ticker.stop()
    }

    /**
     * Manually requests a single frame tick.
     */
    fun requestFrame() {
        ticker.requestTick()
    }

    /**
     * Triggers the rendering of a single frame.
     */
    fun onDrawFrame(frameTimeNanos: Long = System.nanoTime()) {
        state.currentTimeNanos = frameTimeNanos
        
        // 1. Update Theme Policy (Centralized)
        AtmosphericThemeMapper.updatePalette(state)

        val canvas = renderSurface.lockCanvas()

        canvas?.let {
            try {
                // 2. Update and Draw layers
                layerManager.updateAndDraw(state, it)
            } catch (e: Throwable) {
                // Failsafe: if the complex render loop fails, draw the fallback
                fallbackRenderer.updateAndDraw(state, it)
            } finally {
                renderSurface.unlockCanvasAndPost(it)
            }
        }
    }

    /**
     * Updates the surface dimensions and density.
     */
    fun onSurfaceChanged(width: Int, height: Int, density: Float) {
        state.surfaceWidth = width
        state.surfaceHeight = height
        state.surfaceDensity = density

        // 1. Calculate Scale Factor (Shared Logic)
        val scaleX = width / state.referenceWidth
        val scaleY = height / state.referenceHeight
        val scale = when (targetScaleMode) {
            ScaleMode.FIT -> min(scaleX, scaleY)
            ScaleMode.FILL -> max(scaleX, scaleY)
        }

        // 2. Update Viewport Metrics (Geometry Handover)
        state.viewportReferenceScale = scale
        state.viewportSafeWidth = state.referenceWidth * scale
        state.viewportSafeHeight = state.referenceHeight * scale
        state.viewportSafeX = (width - state.viewportSafeWidth) / 2f
        state.viewportSafeY = (height - state.viewportSafeHeight) / 2f

        // 3. Propagate to renderers
        layerManager.onSurfaceChanged(width, height, density)
        fallbackRenderer.onSurfaceChanged(width, height, density)
    }

    /**
     * Adds an effect layer to the engine.
     */
    fun addEffect(renderer: EffectRenderer) {
        layerManager.addEffect(renderer)
    }

    /**
     * Removes an effect layer from the engine.
     */
    fun removeEffect(renderer: EffectRenderer) {
        layerManager.removeEffect(renderer)
    }

    /**
     * Called when the engine is destroyed.
     * Clean up resources and cancel any pending work.
     */
    fun onDestroy() {
        stop()
    }
}
