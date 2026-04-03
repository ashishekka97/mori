package me.ashishekka.mori.engine

import android.graphics.Canvas
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.ashishekka.mori.engine.core.interfaces.AssetRegistry
import me.ashishekka.mori.engine.core.models.AtlasRegion
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SurfaceHolderRenderSurfaceTest {

    private val mockAssetRegistry = mockk<AssetRegistry>(relaxed = true)
    private val mockServiceEngine = mockk<WallpaperService.Engine>()
    private val mockSurfaceHolder = mockk<SurfaceHolder>()
    private val mockNativeCanvas = mockk<Canvas>()
    private val renderSurface = SurfaceHolderRenderSurface(mockServiceEngine, mockAssetRegistry)

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
        val mockAndroidCanvas = AndroidEngineCanvas(mockNativeCanvas, mockAssetRegistry)

        // When
        renderSurface.unlockCanvasAndPost(mockAndroidCanvas)

        // Then
        verify { mockSurfaceHolder.unlockCanvasAndPost(mockNativeCanvas) }
    }

    @Test
    fun `drawShader should return early if SDK is below Tiramisu`() {
        // Given
        val mockAndroidCanvas = AndroidEngineCanvas(mockNativeCanvas, mockAssetRegistry)
        val uniforms = FloatArray(16)

        // When
        // In JVM tests, Build.VERSION.SDK_INT evaluates to 0, so it should return early
        mockAndroidCanvas.drawShader(1, 0f, 0f, 100f, 100f, uniforms, 1.0f)

        // Then
        // Verify that nativeCanvas.drawRect is NOT called
        verify(exactly = 0) { mockNativeCanvas.drawRect(any(), any(), any(), any(), any()) }
    }

    private fun assertEquals(expected: Any?, actual: Any?) {
        org.junit.Assert.assertEquals(expected, actual)
    }
}
