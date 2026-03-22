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
 */
class MoriEngine(
    private val ticker: EngineTicker,
    private val renderSurface: RenderSurface,
    private val layerManager: LayerManager,
    private val fallbackRenderer: EffectRenderer = StaticFallbackRenderer()
) {

    private var isRunning = false
    val state = MoriEngineState()

    // Geometric Configuration
    var targetScaleMode: ScaleMode = ScaleMode.FIT

    // FPS Control
    var targetFps: Int = 60
        set(value) {
            field = value.coerceIn(1, 120)
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
     * Applies a complete wallpaper definition to the engine.
     */
    fun setWallpaper(wallpaper: MoriWallpaper) {
        layerManager.clear()
        // We use the spec's base color as the initial foundation
        state.dominantFoundationColor = wallpaper.baseBackgroundColor
        wallpaper.layers.forEach { layerManager.addEffect(it) }
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
        
        // 1. Update Theme Policy (Sets the foundation color)
        AtmosphericThemeMapper.updatePalette(state)

        val canvas = renderSurface.lockCanvas()

        canvas?.let {
            try {
                // 2. Draw Opaque Foundation FIRST
                it.drawColor(state.dominantFoundationColor)
                
                // 3. Update and Draw layers
                layerManager.updateAndDraw(state, it)
            } catch (e: Throwable) {
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

        val scaleX = width / state.referenceWidth
        val scaleY = height / state.referenceHeight
        val scale = when (targetScaleMode) {
            ScaleMode.FIT -> min(scaleX, scaleY)
            ScaleMode.FILL -> max(scaleX, scaleY)
        }

        state.viewportReferenceScale = scale
        state.viewportSafeWidth = state.referenceWidth * scale
        state.viewportSafeHeight = state.referenceHeight * scale
        state.viewportSafeX = (width - state.viewportSafeWidth) / 2f
        state.viewportSafeY = (height - state.viewportSafeHeight) / 2f

        layerManager.onSurfaceChanged(width, height, density)
        fallbackRenderer.onSurfaceChanged(width, height, density)
    }

    fun addEffect(renderer: EffectRenderer) {
        layerManager.addEffect(renderer)
    }

    fun removeEffect(renderer: EffectRenderer) {
        layerManager.removeEffect(renderer)
    }

    fun onDestroy() {
        stop()
    }
}
