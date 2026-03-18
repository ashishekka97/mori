package me.ashishekka.mori.engine.core

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import me.ashishekka.mori.engine.core.interfaces.EngineTicker
import me.ashishekka.mori.engine.core.interfaces.RenderSurface
import me.ashishekka.mori.engine.renderer.EffectRenderer
import me.ashishekka.mori.engine.renderer.LayerManager
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MoriEngineTest {

    private lateinit var mockTicker: EngineTicker
    private lateinit var mockRenderSurface: RenderSurface
    private lateinit var mockCanvas: EngineCanvas
    private lateinit var layerManager: LayerManager
    private lateinit var mockFallbackRenderer: EffectRenderer
    private lateinit var engine: MoriEngine

    private val tickCallbackSlot = slot<(Long) -> Unit>()

    @Before
    fun setUp() {
        mockTicker = mockk(relaxed = true)
        mockRenderSurface = mockk(relaxed = true)
        mockCanvas = mockk<EngineCanvas>(relaxed = true)
        layerManager = LayerManager()
        mockFallbackRenderer = mockk<EffectRenderer>(relaxed = true)

        every { mockTicker.setOnTickCallback(capture(tickCallbackSlot)) } returns Unit
        
        engine = MoriEngine(mockTicker, mockRenderSurface, layerManager, mockFallbackRenderer)
    }

    @Test
    fun `start should delegate to ticker`() {
        engine.start()
        verify { mockTicker.start() }
    }

    @Test
    fun `stop should delegate to ticker`() {
        engine.start()
        engine.stop()
        verify { mockTicker.stop() }
    }

    @Test
    fun `setContinuousRendering should delegate to ticker`() {
        engine.setContinuousRendering(false)
        verify { mockTicker.setContinuous(false) }
    }

    @Test
    fun `requestFrame should delegate to ticker`() {
        engine.requestFrame()
        verify { mockTicker.requestTick() }
    }

    @Test
    fun `tick should not trigger draw if stopped`() {
        // Given
        engine.start()
        engine.stop()

        // When
        tickCallbackSlot.captured.invoke(1_000_000_000L)

        // Then
        verify(exactly = 0) { mockRenderSurface.lockCanvas() }
    }

    @Test
    fun `tick should trigger draw when interval is met`() {
        // Given
        engine.start()
        every { mockRenderSurface.lockCanvas() } returns mockCanvas

        // When (Trigger first frame - 1 second mark)
        tickCallbackSlot.captured.invoke(1_000_000_000L)

        // Then
        verify(exactly = 1) { mockRenderSurface.lockCanvas() }
    }

    @Test
    fun `tick should skip draw if interval is too small (30FPS)`() {
        // Given (30 FPS = ~33.3ms interval)
        engine.start()
        engine.targetFps = 30
        every { mockRenderSurface.lockCanvas() } returns mockCanvas

        // Trigger first frame at 1s mark
        tickCallbackSlot.captured.invoke(1_000_000_000L)
        verify(exactly = 1) { mockRenderSurface.lockCanvas() }

        // When (Trigger second frame only 16ms later)
        // 1,016,000,000L - 1,000,000,000L = 16ms < 33ms
        tickCallbackSlot.captured.invoke(1_016_000_000L)

        // Then (Should still only have 1 draw call total)
        verify(exactly = 1) { mockRenderSurface.lockCanvas() }
    }

    @Test
    fun `tick should draw after correct interval (30FPS)`() {
        // Given (30 FPS = ~33.3ms interval)
        engine.start()
        engine.targetFps = 30
        every { mockRenderSurface.lockCanvas() } returns mockCanvas

        // Trigger first frame at 1s mark
        tickCallbackSlot.captured.invoke(1_000_000_000L)
        
        // When (Trigger second frame 40ms later)
        // 1,040,000,000L - 1,000,000,000L = 40ms > 33ms
        tickCallbackSlot.captured.invoke(1_040_000_000L)

        // Then (Should have 2 draw calls total)
        verify(exactly = 2) { mockRenderSurface.lockCanvas() }
    }

    @Test
    fun `onDrawFrame should use fallback when drawing fails`() {
        // Given
        engine.start()
        // Force an effect that fails to trigger fallback
        val mockEffect = mockk<EffectRenderer>(relaxed = true)
        every { mockEffect.updateAndDraw(any(), any()) } throws RuntimeException("Draw failure")
        engine.addEffect(mockEffect)

        every { mockRenderSurface.lockCanvas() } returns mockCanvas

        // When
        engine.onDrawFrame()

        // Then
        verify(exactly = 1) { mockFallbackRenderer.updateAndDraw(any(), mockCanvas) }
        verify { mockRenderSurface.unlockCanvasAndPost(mockCanvas) }
    }

    @Test
    fun `onDrawFrame should post to surface on success`() {
        // Given
        engine.start()
        every { mockRenderSurface.lockCanvas() } returns mockCanvas

        // When
        engine.onDrawFrame()

        // Then
        verify(exactly = 1) { mockRenderSurface.unlockCanvasAndPost(mockCanvas) }
    }

    @Test
    fun `addEffect should return true when added to layer manager`() {
        // Given
        val mockEffect = mockk<EffectRenderer>(relaxed = true)

        // When
        val result = engine.addEffect(mockEffect)

        // Then
        assertTrue(result)
    }

    @Test
    fun `onDrawFrame should draw all layers`() {
        // Given
        engine.start()
        val mockEffect = mockk<EffectRenderer>(relaxed = true)
        engine.addEffect(mockEffect)
        every { mockRenderSurface.lockCanvas() } returns mockCanvas

        // When
        engine.onDrawFrame()

        // Then
        verify(exactly = 1) { mockEffect.updateAndDraw(any(), mockCanvas) }
    }

    @Test
    fun `onDrawFrame should use fallback when layer manager fails`() {
        // Given
        engine.start()
        val mockEffect = mockk<EffectRenderer>(relaxed = true)
        engine.addEffect(mockEffect)
        every { mockEffect.updateAndDraw(any(), any()) } throws RuntimeException("Layer failure")
        every { mockRenderSurface.lockCanvas() } returns mockCanvas

        // When
        engine.onDrawFrame()

        // Then
        verify(exactly = 1) { mockFallbackRenderer.updateAndDraw(any(), mockCanvas) }
    }

    @Test
    fun `onSurfaceChanged should propagate to fallback renderer`() {
        // When
        engine.onSurfaceChanged(1080, 1920, 2.5f)

        // Then
        verify { mockFallbackRenderer.onSurfaceChanged(1080, 1920, 2.5f) }
    }

    @Test
    fun `onDestroy should stop engine`() {
        // Given
        engine.start()
        
        // When
        engine.onDestroy()
        
        // Then
        verify { mockTicker.stop() }
    }
}

