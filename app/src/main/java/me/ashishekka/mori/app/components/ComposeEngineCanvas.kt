package me.ashishekka.mori.app.components

import android.graphics.Bitmap
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import me.ashishekka.mori.engine.core.interfaces.AssetRegistry
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import me.ashishekka.mori.engine.core.models.RenderProperty
import me.ashishekka.mori.engine.core.models.ShaderUniforms

/**
 * A bridge between Mori's platform-agnostic [EngineCanvas] and Compose's [DrawScope].
 * This is designed for zero-allocation performance by reusing the same instance
 * and updating its [drawScope] on every frame.
 */
class ComposeEngineCanvas(
    private val assetRegistry: AssetRegistry
) : EngineCanvas {
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

    private val cachedComposePaths = mutableMapOf<Int, Path>()

    private var cachedAtlas: Bitmap? = null
    private var cachedImageBitmap: ImageBitmap? = null
    
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

    override fun drawBitmap(resId: Int, left: Float, top: Float, right: Float, bottom: Float, alpha: Float) {
        val atlas = assetRegistry.getAtlas() as? Bitmap ?: return
        
        // Cache ImageBitmap to avoid re-wrapping if the atlas hasn't changed
        if (atlas !== cachedAtlas) {
            cachedAtlas = atlas
            cachedImageBitmap = atlas.asImageBitmap()
        }
        
        val imageBitmap = cachedImageBitmap ?: return
        val region = assetRegistry.getAtlasRegion(resId)
        
        if (region.width <= 0 || region.height <= 0) return

        drawScope?.applyTransformAndDraw {
            drawImage(
                image = imageBitmap,
                srcOffset = IntOffset(region.left, region.top),
                srcSize = IntSize(region.width, region.height),
                dstOffset = IntOffset(left.toInt(), top.toInt()),
                dstSize = IntSize((right - left).toInt(), (bottom - top).toInt()),
                alpha = alpha
            )
        }
    }

    override fun drawShader(resId: Int, left: Float, top: Float, right: Float, bottom: Float, uniforms: FloatArray, complexity: Float) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val shader = assetRegistry.getShader(resId) as? RuntimeShader ?: return

        try {
            shader.setFloatUniform(ShaderUniforms.COMPLEXITY, complexity)
        } catch (e: IllegalArgumentException) {
            // Ignore
        }

        for (i in 0 until RenderProperty.BUFFER_SIZE) {
            try {
                if (i == RenderProperty.INDEX_COLOR_PRIMARY || i == RenderProperty.INDEX_COLOR_SECONDARY) {
                    val colorInt = java.lang.Float.floatToRawIntBits(uniforms[i])
                    shader.setColorUniform(ShaderUniforms.UNIFORM_NAMES[i], colorInt)
                } else {
                    shader.setFloatUniform(ShaderUniforms.UNIFORM_NAMES[i], uniforms[i])
                }
            } catch (e: IllegalArgumentException) {
                // Ignore uniforms that are not defined in the AGSL shader
            }
        }

        drawScope?.applyTransformAndDraw {
            drawRect(
                brush = ShaderBrush(shader),
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top)
            )
        }
    }

    override fun drawPath(resId: Int, color: Int, isFilled: Boolean, thickness: Float) {
        val nativePath = assetRegistry.getStoredPath(resId) as? android.graphics.Path ?: return

        var composePath = cachedComposePaths.get(resId)
        if (composePath == null) {
            composePath = nativePath.asComposePath()
            cachedComposePaths.put(resId, composePath)
        }

        val style = if (isFilled) Fill else Stroke(width = thickness)
        drawScope?.applyTransformAndDraw {
            drawPath(composePath, Color(color), style = style)
        }
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
