package me.ashishekka.mori.engine

import android.graphics.Canvas
import android.graphics.Paint
import android.service.wallpaper.WallpaperService
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import me.ashishekka.mori.engine.core.interfaces.RenderSurface

/**
 * Android implementation of [EngineCanvas] wrapping a native [Canvas].
 */
class AndroidEngineCanvas(val nativeCanvas: Canvas) : EngineCanvas {
    
    private val paint = Paint()

    override fun drawColor(colorInt: Int) {
        nativeCanvas.drawColor(colorInt)
    }

    override fun drawRect(left: Float, top: Float, right: Float, bottom: Float, color: Int, isFilled: Boolean) {
        paint.color = color
        paint.style = if (isFilled) Paint.Style.FILL else Paint.Style.STROKE
        paint.strokeWidth = if (isFilled) 0f else 4f
        nativeCanvas.drawRect(left, top, right, bottom, paint)
    }

    override fun drawCircle(centerX: Float, centerY: Float, radius: Float, color: Int, isFilled: Boolean) {
        paint.color = color
        paint.style = if (isFilled) Paint.Style.FILL else Paint.Style.STROKE
        paint.strokeWidth = if (isFilled) 0f else 4f
        nativeCanvas.drawCircle(centerX, centerY, radius, paint)
    }
}

/**
 * Android implementation of [RenderSurface] using a [WallpaperService.Engine]'s SurfaceHolder.
 */
class SurfaceHolderRenderSurface(
    private val serviceEngine: WallpaperService.Engine
) : RenderSurface {

    override fun lockCanvas(): EngineCanvas? {
        val nativeCanvas = try {
            serviceEngine.surfaceHolder.lockCanvas()
        } catch (e: Exception) {
            null
        }
        return nativeCanvas?.let { AndroidEngineCanvas(it) }
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
