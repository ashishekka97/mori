package me.ashishekka.mori.engine

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.RuntimeShader
import android.os.Build
import android.service.wallpaper.WallpaperService
import me.ashishekka.mori.engine.core.interfaces.AssetRegistry
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import me.ashishekka.mori.engine.core.interfaces.RenderSurface

/**
 * Android implementation of [EngineCanvas] wrapping a native [Canvas].
 */
class AndroidEngineCanvas(
    val nativeCanvas: Canvas,
    private val assetRegistry: AssetRegistry
) : EngineCanvas {
    
    private val paint = Paint()
    private val path = Path()
    private val srcRect = Rect()
    private val dstRect = RectF()

    override fun drawColor(colorInt: Int) {
        nativeCanvas.drawColor(colorInt)
    }

    override fun drawRect(left: Float, top: Float, right: Float, bottom: Float, color: Int, isFilled: Boolean, thickness: Float) {
        paint.color = color
        paint.style = if (isFilled) Paint.Style.FILL else Paint.Style.STROKE
        paint.strokeWidth = if (isFilled) 0f else thickness
        nativeCanvas.drawRect(left, top, right, bottom, paint)
    }

    override fun drawCircle(centerX: Float, centerY: Float, radius: Float, color: Int, isFilled: Boolean, thickness: Float) {
        paint.color = color
        paint.style = if (isFilled) Paint.Style.FILL else Paint.Style.STROKE
        paint.strokeWidth = if (isFilled) 0f else thickness
        nativeCanvas.drawCircle(centerX, centerY, radius, paint)
    }

    override fun drawPolygon(points: FloatArray, pointCount: Int, color: Int, isFilled: Boolean, thickness: Float) {
        if (pointCount < 4 || points.size < pointCount) return
        
        paint.color = color
        paint.style = if (isFilled) Paint.Style.FILL else Paint.Style.STROKE
        paint.strokeWidth = if (isFilled) 0f else thickness
        
        path.reset()
        path.moveTo(points[0], points[1])
        
        var i = 2
        while (i < pointCount - 1) {
            path.lineTo(points[i], points[i + 1])
            i += 2
        }
        
        if (isFilled) {
            path.close()
        }
        
        nativeCanvas.drawPath(path, paint)
    }

    override fun drawBitmap(resId: Int, left: Float, top: Float, right: Float, bottom: Float, alpha: Float) {
        val atlas = assetRegistry.getAtlas() as? Bitmap ?: return
        val region = assetRegistry.getAtlasRegion(resId)
        
        if (region.width <= 0 || region.height <= 0) return

        val src = Rect(region.left, region.top, region.left + region.width, region.top + region.height)
        val dst = RectF(left, top, right, bottom)
        
        paint.alpha = (alpha * 255).toInt()
        nativeCanvas.drawBitmap(atlas, src, dst, paint)
    }

    override fun drawShader(resId: Int, left: Float, top: Float, right: Float, bottom: Float, uniforms: FloatArray) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val shader = assetRegistry.getShader(resId) as? RuntimeShader ?: return

        // Basic uniform mapping
        // Task 7.2.2 will expand this, but for now we map the core properties
        // based on the PropertyBuffer indices defined in the Rule Engine.
        shader.setFloatUniform("u_alpha", uniforms[4]) // INDEX_ALPHA
        // Custom expansion slots
        shader.setFloatUniform("u_custom_a", uniforms[11]) // INDEX_CUSTOM_A
        shader.setFloatUniform("u_custom_b", uniforms[12]) // INDEX_CUSTOM_B
        shader.setFloatUniform("u_custom_c", uniforms[13]) // INDEX_CUSTOM_C
        shader.setFloatUniform("u_custom_d", uniforms[14]) // INDEX_CUSTOM_D
        shader.setFloatUniform("u_custom_e", uniforms[15]) // INDEX_CUSTOM_E

        paint.shader = shader
        nativeCanvas.drawRect(left, top, right, bottom, paint)
        paint.shader = null
    }

    override fun save() {
        nativeCanvas.save()
    }

    override fun restore() {
        nativeCanvas.restore()
    }

    override fun rotate(degrees: Float, pivotX: Float, pivotY: Float) {
        nativeCanvas.rotate(degrees, pivotX, pivotY)
    }

    override fun translate(dx: Float, dy: Float) {
        nativeCanvas.translate(dx, dy)
    }

    override fun scale(sx: Float, sy: Float, pivotX: Float, pivotY: Float) {
        nativeCanvas.scale(sx, sy, pivotX, pivotY)
    }
}

/**
 * Android implementation of [RenderSurface] using a [WallpaperService.Engine]'s SurfaceHolder.
 */
class SurfaceHolderRenderSurface(
    private val serviceEngine: WallpaperService.Engine,
    private val assetRegistry: AssetRegistry
) : RenderSurface {

    override fun lockCanvas(): EngineCanvas? {
        val nativeCanvas = try {
            serviceEngine.surfaceHolder.lockCanvas()
        } catch (e: Exception) {
            null
        }
        return nativeCanvas?.let { AndroidEngineCanvas(it, assetRegistry) }
    }

    override fun unlockCanvasAndPost(canvas: EngineCanvas) {
        if (canvas is AndroidEngineCanvas) {
            try {
                serviceEngine.surfaceHolder.unlockCanvasAndPost(canvas.nativeCanvas)
            } catch (e: Exception) {
                // Ignore surface invalidation errors
            }
        }
    }
}
