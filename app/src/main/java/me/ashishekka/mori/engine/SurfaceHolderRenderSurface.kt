package me.ashishekka.mori.engine

import android.graphics.Canvas
import android.service.wallpaper.WallpaperService
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import me.ashishekka.mori.engine.core.interfaces.RenderSurface

/**
 * Android implementation of [EngineCanvas] wrapping a native [Canvas].
 */
class AndroidEngineCanvas(val nativeCanvas: Canvas) : EngineCanvas {
    override fun drawColor(colorInt: Int) {
        nativeCanvas.drawColor(colorInt)
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
