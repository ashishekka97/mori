package me.ashishekka.mori.engine.core

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import me.ashishekka.mori.engine.core.interfaces.AssetRegistry
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import me.ashishekka.mori.engine.core.interfaces.EngineTicker
import me.ashishekka.mori.engine.core.interfaces.RenderSurface
import me.ashishekka.mori.engine.core.models.ScaleMode
import me.ashishekka.mori.engine.renderer.EffectRenderer
import me.ashishekka.mori.engine.renderer.LayerManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MoriEngineTest {

    private lateinit var ticker: EngineTicker
    private lateinit var renderSurface: RenderSurface
    private lateinit var layerManager: LayerManager
    private lateinit var assetRegistry: AssetRegistry
    private lateinit var fallbackRenderer: EffectRenderer
    private lateinit var engine: MoriEngine

    @Before
    fun setUp() {
        ticker = mockk(relaxed = true)
        renderSurface = mockk(relaxed = true)
        layerManager = mockk(relaxed = true)
        assetRegistry = mockk(relaxed = true)
        fallbackRenderer = mockk(relaxed = true)
        engine = MoriEngine(ticker, renderSurface, layerManager, assetRegistry, fallbackRenderer)
    }

    @Test
    fun `start should start ticker`() {
        engine.start()
        verify { ticker.start() }
    }

    @Test
    fun `stop should stop ticker`() {
        engine.start()
        engine.stop()
        verify { ticker.stop() }
    }

    @Test
    fun `onDrawFrame should update currentTimeNanos in state`() {
        val testTime = 123456789L
        engine.onDrawFrame(testTime)

        assertEquals(testTime, engine.state.currentTimeNanos)
    }

    @Test
    fun `onDrawFrame should follow update-then-draw cycle`() {
        // Given
        val mockCanvas = mockk<EngineCanvas>()
        every { renderSurface.lockCanvas() } returns mockCanvas

        // When
        engine.onDrawFrame()

        // Then
        verify(exactly = 1) { layerManager.update(any()) }
        verify(exactly = 1) { layerManager.draw(mockCanvas) }
        verify(exactly = 1) { renderSurface.unlockCanvasAndPost(mockCanvas) }
    }

    @Test
    fun `onDrawFrame should use fallback renderer if main draw fails`() {
        // Given
        val mockCanvas = mockk<EngineCanvas>()
        every { renderSurface.lockCanvas() } returns mockCanvas
        every { layerManager.draw(any()) } throws RuntimeException("GPU Crash")

        // When
        engine.onDrawFrame()

        // Then
        verify { fallbackRenderer.update(any()) }
        verify { fallbackRenderer.render(mockCanvas) }
        verify { renderSurface.unlockCanvasAndPost(mockCanvas) }
    }

    @Test
    fun `ticker callback should respect target FPS`() {
        // Capture the ticker callback
        val tickCallbackSlot = slot<(Long) -> Unit>()
        verify { ticker.setOnTickCallback(capture(tickCallbackSlot)) }
        val tickCallback = tickCallbackSlot.captured

        engine.targetFps = 30 // 33.33ms interval
        engine.start()

        // First tick (always draws)
        tickCallback(100_000_000L)
        verify(exactly = 1) { layerManager.update(any()) }

        // Second tick too soon (10ms later)
        tickCallback(110_000_000L)
        verify(exactly = 1) { layerManager.update(any()) } // Still 1

        // Third tick far enough (40ms later)
        tickCallback(150_000_000L)
        verify(exactly = 2) { layerManager.update(any()) }
    }

    @Test
    fun `onSurfaceChanged with FIT should calculate correct viewport`() {
        // Design: 1000x1000. Screen: 2000x1000 (Landscape)
        engine.state.referenceWidth = 1000f
        engine.state.referenceHeight = 1000f
        engine.targetScaleMode = ScaleMode.FIT

        engine.onSurfaceChanged(2000, 1000, 1.0f)

        // Scale should be 1.0 (to fit the 1000 height)
        assertEquals(1.0f, engine.state.viewportReferenceScale)
        // Offset should be (2000 - 1000) / 2 = 500
        assertEquals(500f, engine.state.viewportSafeX)
        assertEquals(0f, engine.state.viewportSafeY)
    }

    @Test
    fun `onSurfaceChanged with FILL should calculate correct viewport`() {
        // Design: 1000x1000. Screen: 2000x1000 (Landscape)
        engine.state.referenceWidth = 1000f
        engine.state.referenceHeight = 1000f
        engine.targetScaleMode = ScaleMode.FILL

        engine.onSurfaceChanged(2000, 1000, 1.0f)

        // Scale should be 2.0 (to fill the 2000 width)
        assertEquals(2.0f, engine.state.viewportReferenceScale)
        // Offset should be 0 for X, and (1000 - 2000) / 2 = -500 for Y
        assertEquals(0f, engine.state.viewportSafeX)
        assertEquals(-500f, engine.state.viewportSafeY)
    }

    @Test
    fun `onDestroy should stop ticker and clear asset registry`() {
        engine.start()
        engine.onDestroy()
        
        verify { ticker.stop() }
        verify { assetRegistry.clear() }
    }

    @Test
    fun `onDrawFrame should calculate shaderComplexity based on battery and thermal stress`() {
        // Default (isCharging = true, thermal = 0) -> 1.0f
        engine.state.setFieldValue(MoriEngineStateIndices.FACT_IS_CHARGING, 1.0f)
        engine.state.setFieldValue(MoriEngineStateIndices.FACT_THERMAL_STRESS, 0.0f)
        engine.onDrawFrame()
        assertEquals(1.0f, engine.state.shaderComplexity)

        // Thermal stress > 0.4 -> capped at 0.2f
        engine.state.setFieldValue(MoriEngineStateIndices.FACT_THERMAL_STRESS, 0.5f)
        engine.onDrawFrame()
        assertEquals(0.2f, engine.state.shaderComplexity)

        // Thermal stress > 0.2 -> capped at 0.5f
        engine.state.setFieldValue(MoriEngineStateIndices.FACT_THERMAL_STRESS, 0.3f)
        engine.onDrawFrame()
        assertEquals(0.5f, engine.state.shaderComplexity)

        // Battery < 15% and not charging -> capped at 0.3f
        engine.state.setFieldValue(MoriEngineStateIndices.FACT_THERMAL_STRESS, 0.0f)
        engine.state.setFieldValue(MoriEngineStateIndices.FACT_IS_CHARGING, 0.0f)
        engine.state.setFieldValue(MoriEngineStateIndices.FACT_BATTERY_LEVEL, 0.10f)
        engine.onDrawFrame()
        assertEquals(0.3f, engine.state.shaderComplexity)

        // Battery < 50% and not charging -> capped at 0.7f
        engine.state.setFieldValue(MoriEngineStateIndices.FACT_BATTERY_LEVEL, 0.40f)
        engine.onDrawFrame()
        assertEquals(0.7f, engine.state.shaderComplexity)
    }
}
