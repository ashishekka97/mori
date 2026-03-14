package me.ashishekka.mori.engine

import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import androidx.core.graphics.toColorInt

class MoriWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return MoriEngine()
    }

    private inner class MoriEngine : Engine() {

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            // Pre-allocate your paints, paths, and bitmaps here. No 'new' allowed after this!
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                // Screen turned on / went to home screen -> Start the render loop Coroutine
                drawDummyFrame()
            } else {
                // App opened / Screen locked -> Kill the render loop immediately to save battery
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            drawDummyFrame()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            // Clean up resources, cancel coroutines
        }

        private fun drawDummyFrame() {
            val holder = surfaceHolder
            var canvas = try {
                holder.lockCanvas()
            } catch (e: Exception) {
                null
            }

            canvas?.let {
                it.drawColor("#121212".toColorInt()) // Mori Dark Grey
                holder.unlockCanvasAndPost(it)
            }
        }
    }
}
