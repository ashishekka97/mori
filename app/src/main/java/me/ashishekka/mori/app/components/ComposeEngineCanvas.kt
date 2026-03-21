package me.ashishekka.mori.app.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas

/**
 * A bridge between Mori's platform-agnostic [EngineCanvas] and Compose's [DrawScope].
 * This is designed for zero-allocation performance by reusing the same instance
 * and updating its [drawScope] on every frame.
 */
class ComposeEngineCanvas : EngineCanvas {
    var drawScope: DrawScope? = null

    override fun drawColor(colorInt: Int) {
        drawScope?.drawRect(
            color = Color(colorInt)
        )
    }

    override fun drawRect(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        color: Int,
        isFilled: Boolean,
        thickness: Float
    ) {
        val style = if (isFilled) Fill else Stroke(width = thickness)
        drawScope?.drawRect(
            color = Color(color),
            topLeft = Offset(left, top),
            size = Size(right - left, bottom - top),
            style = style
        )
    }

    override fun drawCircle(
        centerX: Float,
        centerY: Float,
        radius: Float,
        color: Int,
        isFilled: Boolean,
        thickness: Float
    ) {
        val style = if (isFilled) Fill else Stroke(width = thickness)
        drawScope?.drawCircle(
            color = Color(color),
            radius = radius,
            center = Offset(centerX, centerY),
            style = style
        )
    }
}
