package me.ashishekka.mori.engine.core

import me.ashishekka.mori.engine.core.interfaces.AssetRegistry
import me.ashishekka.mori.engine.core.interfaces.EngineTicker
import me.ashishekka.mori.engine.core.interfaces.RenderSurface
import me.ashishekka.mori.engine.core.models.ScaleMode
import me.ashishekka.mori.engine.renderer.EffectRenderer
import me.ashishekka.mori.engine.renderer.LayerManager
import me.ashishekka.mori.engine.renderer.StaticFallbackRenderer
import kotlin.math.max
import kotlin.math.min

/**
 * The core rendering engine for Mori.
 * A platform-agnostic orchestrator that delegates visual responsibility to [MoriWallpaper].
 */
class MoriEngine(
    private val ticker: EngineTicker,
    private val renderSurface: RenderSurface,
    private val layerManager: LayerManager,
    val assetRegistry: AssetRegistry,
    private val fallbackRenderer: EffectRenderer = StaticFallbackRenderer()
) {

    private var isRunning = false
    val state = MoriEngineState()

    var targetScaleMode: ScaleMode = ScaleMode.FIT
    var currentWallpaper: MoriWallpaper? = null
        private set

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
     * ZERO-ALLOCATION: Uses manual indexing to avoid iterator creation.
     */
    fun setWallpaper(wallpaper: MoriWallpaper) {
        this.currentWallpaper = wallpaper
        layerManager.clear()
        
        var i = 0
        val size = wallpaper.layers.size
        while (i < size) {
            layerManager.addEffect(wallpaper.layers[i])
            i++
        }
    }

    fun start() {
        if (isRunning) return
        isRunning = true
        ticker.start()
    }

    fun stop() {
        if (!isRunning) return
        isRunning = false
        ticker.stop()
    }

    fun requestFrame() {
        ticker.requestTick()
    }

    /**
     * Triggers the rendering of a single frame using a stable Update-then-Draw cycle.
     */
    fun onDrawFrame(frameTimeNanos: Long = System.nanoTime()) {
        state.currentTimeNanos = frameTimeNanos
        
        // Update timeSeconds for smooth DSL animations
        state.timeSeconds = frameTimeNanos / 1_000_000_000f
        
        // 1. UPDATE: Propagate the latest state to all layers first.
        layerManager.update(state)
        
        // 2. SYNTHESIZE: Let the wallpaper derive its theme from the now-updated layers.
        currentWallpaper?.synthesizePalette(state)

        val canvas = renderSurface.lockCanvas()

        canvas?.let {
            try {
                // 3. DRAW: Render all layers onto the canvas.
                layerManager.draw(it)
            } catch (e: Throwable) {
                fallbackRenderer.update(state)
                fallbackRenderer.render(it)
            } finally {
                renderSurface.unlockCanvasAndPost(it)
            }
        }
    }

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
        assetRegistry.clear()
    }
}
