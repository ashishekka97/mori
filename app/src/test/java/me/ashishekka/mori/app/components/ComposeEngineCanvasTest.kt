package me.ashishekka.mori.app.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import me.ashishekka.mori.engine.core.interfaces.AssetRegistry
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class ComposeEngineCanvasTest {

    private val mockAssetRegistry = mockk<AssetRegistry>(relaxed = true)
    private val canvas = ComposeEngineCanvas(mockAssetRegistry)
    private val mockDrawScope = mockk<DrawScope>(relaxed = true)

    @Test
    fun `drawColor should call drawRect on drawScope`() {
        canvas.drawScope = mockDrawScope
        val colorInt = 0xFFFF0000.toInt()

        canvas.drawColor(colorInt)

        verify { mockDrawScope.drawRect(color = Color(colorInt)) }
    }

    @Test
    fun `drawRect should call drawRect on drawScope with correct params`() {
        canvas.drawScope = mockDrawScope
        val colorInt = 0xFF00FF00.toInt()

        canvas.drawRect(10f, 20f, 110f, 120f, colorInt, true)

        verify {
            mockDrawScope.drawRect(
                color = Color(colorInt),
                topLeft = Offset(10f, 20f),
                size = Size(100f, 100f),
                style = Fill
            )
        }
    }

    @Test
    fun `drawCircle should call drawCircle on drawScope with correct params`() {
        canvas.drawScope = mockDrawScope
        val colorInt = 0xFF0000FF.toInt()

        canvas.drawCircle(50f, 60f, 30f, colorInt, false, 5f)

        verify {
            mockDrawScope.drawCircle(
                color = Color(colorInt),
                radius = 30f,
                center = Offset(50f, 60f),
                style = Stroke(width = 5f)
            )
        }
    }
}
