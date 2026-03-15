package me.ashishekka.mori.engine.core

import android.graphics.Canvas
import android.service.wallpaper.WallpaperService
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
    private lateinit var engine: MoriEngine

    @Before
    fun setUp() {
        mockServiceEngine = mockk(relaxed = true)
        mockSurfaceHolder = mockk(relaxed = true)
        mockCanvas = mockk(relaxed = true)

        every { mockServiceEngine.surfaceHolder } returns mockSurfaceHolder
        
        engine = MoriEngine(mockServiceEngine)
    }

    @Test
    fun `onDrawFrame should lock and unlock canvas when successful`() {
        // Given a successful canvas lock
        every { mockSurfaceHolder.lockCanvas() } returns mockCanvas

        // When
        engine.onDrawFrame()

        // Then
        verify { mockSurfaceHolder.lockCanvas() }
        verify { mockCanvas.drawColor(any<Int>()) }
        verify { mockSurfaceHolder.unlockCanvasAndPost(mockCanvas) }
    }

    @Test
    fun `onDrawFrame should not crash when canvas is null`() {
        // Given a failed canvas lock (returns null)
        every { mockSurfaceHolder.lockCanvas() } returns null

        // When
        engine.onDrawFrame()

        // Then (Verify no interaction with canvas and no crash)
        verify { mockSurfaceHolder.lockCanvas() }
        verify(exactly = 0) { mockSurfaceHolder.unlockCanvasAndPost(any()) }
    }
}
