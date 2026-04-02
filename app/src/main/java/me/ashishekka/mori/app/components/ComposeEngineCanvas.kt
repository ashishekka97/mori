package me.ashishekka.mori.app.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas

/**
 * A bridge between Mori's platform-agnostic [EngineCanvas] and Compose's [DrawScope].
 * This is designed for zero-allocation performance by reusing the same instance
 * and updating its [drawScope] on every frame.
 */
class ComposeEngineCanvas : EngineCanvas {
    var drawScope: DrawScope? = null
    
    private val path = Path()
    private var rotationDegrees: Float = 0f
    private var rotationPivotX: Float = 0f
    private var rotationPivotY: Float = 0f
    private var translateX: Float = 0f
    private var translateY: Float = 0f
    private var scaleX: Float = 1f
    private var scaleY: Float = 1f
    private var scalePivotX: Float = 0f
    private var scalePivotY: Float = 0f
    
    private val hasTransform: Boolean
        get() = rotationDegrees != 0f || translateX != 0f || translateY != 0f || scaleX != 1f || scaleY != 1f

    private inline fun DrawScope.applyTransformAndDraw(drawBlock: DrawScope.() -> Unit) {
        if (hasTransform) {
            withTransform({
                if (translateX != 0f || translateY != 0f) translate(translateX, translateY)
                if (rotationDegrees != 0f) rotate(rotationDegrees, Offset(rotationPivotX, rotationPivotY))
                if (scaleX != 1f || scaleY != 1f) scale(scaleX, scaleY, Offset(scalePivotX, scalePivotY))
            }) {
                drawBlock()
            }
        } else {
            drawBlock()
        }
    }

    override fun drawColor(colorInt: Int) {
        drawScope?.drawRect(
            color = Color(colorInt)
        )
    }

    override fun drawRect(
        left: Float, top: Float, right: Float, bottom: Float,
        color: Int, isFilled: Boolean, thickness: Float
    ) {
        val style = if (isFilled) Fill else Stroke(width = thickness)
        drawScope?.applyTransformAndDraw {
            drawRect(Color(color), Offset(left, top), Size(right - left, bottom - top), style = style)
        }
    }

    override fun drawCircle(
        centerX: Float, centerY: Float, radius: Float,
        color: Int, isFilled: Boolean, thickness: Float
    ) {
        val style = if (isFilled) Fill else Stroke(width = thickness)
        drawScope?.applyTransformAndDraw {
            drawCircle(Color(color), radius, Offset(centerX, centerY), style = style)
        }
    }

    override fun drawPolygon(points: FloatArray, pointCount: Int, color: Int, isFilled: Boolean, thickness: Float) {
        if (pointCount < 4 || points.size < pointCount) return
        val style = if (isFilled) Fill else Stroke(width = thickness)
        
        path.reset()
        path.moveTo(points[0], points[1])
        var i = 2
        while (i < pointCount - 1) {
            path.lineTo(points[i], points[i + 1])
            i += 2
        }
        if (isFilled) path.close()

        drawScope?.applyTransformAndDraw {
            drawPath(path, Color(color), style = style)
        }
    }

    override fun drawBitmap(resId: Int, left: Float, top: Float, alpha: Float) {
        // Phase 7.1.1: Implementation will draw from the TextureAtlas.
    }

    override fun save() {
        // No-op in Compose (DrawScope handles state via withTransform)
    }

    override fun restore() {
        rotationDegrees = 0f
        rotationPivotX = 0f
        rotationPivotY = 0f
        translateX = 0f
        translateY = 0f
        scaleX = 1f
        scaleY = 1f
        scalePivotX = 0f
        scalePivotY = 0f
    }

    override fun rotate(degrees: Float, pivotX: Float, pivotY: Float) {
        rotationDegrees = degrees
        rotationPivotX = pivotX
        rotationPivotY = pivotY
    }

    override fun translate(dx: Float, dy: Float) {
        translateX = dx
        translateY = dy
    }

    override fun scale(sx: Float, sy: Float, pivotX: Float, pivotY: Float) {
        scaleX = sx
        scaleY = sy
        scalePivotX = pivotX
        scalePivotY = pivotY
    }
}
