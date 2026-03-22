package me.ashishekka.mori.engine

import android.graphics.Canvas
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SurfaceHolderRenderSurfaceTest {

    private val mockServiceEngine = mockk<WallpaperService.Engine>()
    private val mockSurfaceHolder = mockk<SurfaceHolder>()
    private val mockNativeCanvas = mockk<Canvas>()
    private val renderSurface = SurfaceHolderRenderSurface(mockServiceEngine)

    @Test
    fun `lockCanvas should return AndroidEngineCanvas wrapping native canvas`() {
        // Given
        every { mockServiceEngine.surfaceHolder } returns mockSurfaceHolder
        every { mockSurfaceHolder.lockCanvas() } returns mockNativeCanvas

        // When
        val engineCanvas = renderSurface.lockCanvas()

        // Then
        assertNotNull(engineCanvas)
        assertTrue(engineCanvas is AndroidEngineCanvas)
        assertEquals(mockNativeCanvas, (engineCanvas as AndroidEngineCanvas).nativeCanvas)
    }

    @Test
    fun `unlockCanvasAndPost should call surfaceHolder unlock`() {
        // Given
        every { mockServiceEngine.surfaceHolder } returns mockSurfaceHolder
        val mockAndroidCanvas = AndroidEngineCanvas(mockNativeCanvas)

        // When
        renderSurface.unlockCanvasAndPost(mockAndroidCanvas)

        // Then
        verify { mockSurfaceHolder.unlockCanvasAndPost(mockNativeCanvas) }
    }

    private fun assertEquals(expected: Any?, actual: Any?) {
        org.junit.Assert.assertEquals(expected, actual)
    }
}
