package me.ashishekka.mori.engine.core

import android.graphics.Canvas
import android.service.wallpaper.WallpaperService
import android.view.Choreographer
import android.view.SurfaceHolder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class MoriEngineTest {

    private lateinit var mockServiceEngine: WallpaperService.Engine
    private lateinit var mockSurfaceHolder: SurfaceHolder
    private lateinit var mockCanvas: Canvas
    private lateinit var mockChoreographer: Choreographer
    private lateinit var engine: MoriEngine

    @Before
    fun setUp() {
        mockServiceEngine = mockk(relaxed = true)
        mockSurfaceHolder = mockk(relaxed = true)
        mockCanvas = mockk(relaxed = true)
        mockChoreographer = mockk<Choreographer>(relaxed = true)

        every { mockServiceEngine.surfaceHolder } returns mockSurfaceHolder
        
        engine = MoriEngine(mockServiceEngine, mockChoreographer)
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
        verify { mockChoreographer.postFrameCallback(engine) } // Verify re-registration
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
        engine.doFrame(1000L)

        // Then
        verify(exactly = 0) { mockSurfaceHolder.lockCanvas() }
        verify(exactly = 0) { mockChoreographer.postFrameCallback(any()) }
    }
}
