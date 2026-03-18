package me.ashishekka.mori.engine.core

import android.graphics.Canvas
import android.service.wallpaper.WallpaperService
import android.view.Choreographer
import android.view.SurfaceHolder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.ashishekka.mori.engine.renderer.EffectRenderer
import org.junit.Before
import org.junit.Test

class MoriEngineTest {

    private lateinit var mockServiceEngine: WallpaperService.Engine
    private lateinit var mockSurfaceHolder: SurfaceHolder
    private lateinit var mockCanvas: Canvas
    private lateinit var mockChoreographer: Choreographer
    private lateinit var mockFallbackRenderer: EffectRenderer
    private lateinit var engine: MoriEngine

    @Before
    fun setUp() {
        mockServiceEngine = mockk(relaxed = true)
        mockSurfaceHolder = mockk(relaxed = true)
        mockCanvas = mockk<Canvas>(relaxed = true)
        mockChoreographer = mockk<Choreographer>(relaxed = true)
        mockFallbackRenderer = mockk<EffectRenderer>(relaxed = true)

        every { mockServiceEngine.surfaceHolder } returns mockSurfaceHolder
        
        engine = MoriEngine(mockServiceEngine, mockChoreographer, mockFallbackRenderer)
    }

    @Test
    fun `start should register frame callback`() {
        engine.start()
        verify { mockChoreographer.postFrameCallback(engine) }
    }

    @Test
    fun `stop should unregister frame callback`() {
        engine.start() // Ensure it is running
        engine.stop()
        verify { mockChoreographer.removeFrameCallback(engine) }
    }

    @Test
    fun `doFrame should trigger draw and re-register callback when running`() {
        // Given
        engine.start()
        every { mockSurfaceHolder.lockCanvas() } returns mockCanvas

        // When (Trigger first frame - 1 second mark)
        engine.doFrame(1_000_000_000L)

        // Then
        verify(exactly = 1) { mockSurfaceHolder.lockCanvas() }
        verify(exactly = 2) { mockChoreographer.postFrameCallback(engine) } // start() + 1x doFrame()
    }

    @Test
    fun `doFrame should skip draw if interval is too small (30FPS)`() {
        // Given (30 FPS = ~33.3ms interval)
        engine.targetFps = 30
        engine.start()
        every { mockSurfaceHolder.lockCanvas() } returns mockCanvas

        // Trigger first frame at 1s mark
        engine.doFrame(1_000_000_000L)
        verify(exactly = 1) { mockSurfaceHolder.lockCanvas() }

        // When (Trigger second frame only 16ms later)
        // 1,016,000,000L - 1,000,000,000L = 16ms < 33ms
        engine.doFrame(1_016_000_000L)

        // Then (Should still only have 1 draw call total)
        verify(exactly = 1) { mockSurfaceHolder.lockCanvas() }
        verify(exactly = 3) { mockChoreographer.postFrameCallback(engine) } // start() + 2x doFrame()
    }

    @Test
    fun `doFrame should draw after correct interval (30FPS)`() {
        // Given (30 FPS = ~33.3ms interval)
        engine.targetFps = 30
        engine.start()
        every { mockSurfaceHolder.lockCanvas() } returns mockCanvas

        // Trigger first frame at 1s mark
        engine.doFrame(1_000_000_000L)
        
        // When (Trigger second frame 40ms later)
        // 1,040,000,000L - 1,000,000,000L = 40ms > 33ms
        engine.doFrame(1_040_000_000L)

        // Then (Should have 2 draw calls total)
        verify(exactly = 2) { mockSurfaceHolder.lockCanvas() }
    }

    @Test
    fun `doFrame should not trigger draw if stopped`() {
        // Given
        engine.stop()

        // When
        engine.doFrame(1_000_000_000L)

        // Then
        verify(exactly = 0) { mockSurfaceHolder.lockCanvas() }
        verify(exactly = 0) { mockChoreographer.postFrameCallback(any()) }
    }

    @Test
    fun `onDrawFrame should use fallback when drawing fails`() {
        // Given
        every { mockSurfaceHolder.lockCanvas() } returns mockCanvas
        // Force an exception during drawColor using answers to avoid immediate execution issues
        every { mockCanvas.drawColor(any<Int>()) } answers { throw RuntimeException("Draw failure") }

        // When
        engine.onDrawFrame()

        // Then
        // Should trigger the fallback renderer
        verify(exactly = 1) { mockFallbackRenderer.updateAndDraw(mockCanvas) }
        verify { mockSurfaceHolder.unlockCanvasAndPost(mockCanvas) }
    }

    @Test
    fun `doFrame should not re-register callback if not continuous`() {
        // Given
        engine.start()
        engine.setContinuousRendering(false)
        every { mockSurfaceHolder.lockCanvas() } returns mockCanvas

        // Clear interactions from start()
        io.mockk.clearMocks(mockChoreographer, answers = false)

        // When
        engine.doFrame(1_000_000_000L)

        // Then (Draw happens, but callback is NOT posted again)
        verify(exactly = 1) { mockSurfaceHolder.lockCanvas() }
        verify(exactly = 0) { mockChoreographer.postFrameCallback(engine) }
    }

    @Test
    fun `requestFrame should unregister and post callback if running`() {
        // Given
        engine.start() // Sets running = true
        io.mockk.clearMocks(mockChoreographer, answers = false)

        // When
        engine.requestFrame()

        // Then
        verify(exactly = 1) { mockChoreographer.removeFrameCallback(engine) }
        verify(exactly = 1) { mockChoreographer.postFrameCallback(engine) }
    }

    @Test
    fun `requestFrame should do nothing if not running`() {
        // Given
        engine.stop() // Sets running = false
        io.mockk.clearMocks(mockChoreographer, answers = false)

        // When
        engine.requestFrame()

        // Then
        verify(exactly = 0) { mockChoreographer.postFrameCallback(engine) }
    }
}

