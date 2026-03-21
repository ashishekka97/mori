package me.ashishekka.mori.app.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class ComposeEngineCanvasTest {

    private lateinit var engineCanvas: ComposeEngineCanvas
    private lateinit var mockDrawScope: DrawScope

    @Before
    fun setUp() {
        engineCanvas = ComposeEngineCanvas()
        mockDrawScope = mockk(relaxed = true)
        engineCanvas.drawScope = mockDrawScope
    }

    @Test
    fun `drawColor should call drawRect on drawScope`() {
        val colorInt = 0xFFFF0000.toInt() // Red
        engineCanvas.drawColor(colorInt)

        verify {
            mockDrawScope.drawRect(
                color = Color(colorInt),
                style = Fill
            )
        }
    }

    @Test
    fun `drawRect should call drawRect on drawScope with correct parameters`() {
        val colorInt = 0xFF00FF00.toInt() // Green
        engineCanvas.drawRect(
            left = 10f,
            top = 20f,
            right = 110f,
            bottom = 120f,
            color = colorInt,
            isFilled = false,
            thickness = 5f
        )

        verify {
            mockDrawScope.drawRect(
                color = Color(colorInt),
                topLeft = Offset(10f, 20f),
                size = Size(100f, 100f),
                style = any<Stroke>()
            )
        }
    }

    @Test
    fun `drawCircle should call drawCircle on drawScope`() {
        val colorInt = 0xFF0000FF.toInt() // Blue
        engineCanvas.drawCircle(
            centerX = 50f,
            centerY = 50f,
            radius = 30f,
            color = colorInt,
            isFilled = true
        )

        verify {
            mockDrawScope.drawCircle(
                color = Color(colorInt),
                radius = 30f,
                center = Offset(50f, 50f),
                style = Fill
            )
        }
    }
}
