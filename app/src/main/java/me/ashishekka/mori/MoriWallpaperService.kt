package me.ashishekka.mori

import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import me.ashishekka.mori.engine.MoriEngine
import me.ashishekka.mori.persona.MoriLifecycleManager

/**
 * The system entry point for the Mori Live Wallpaper.
 * Located in the :app module to orchestrate the [MoriLifecycleManager]
 * and the [MoriEngine].
 */
class MoriWallpaperService : WallpaperService() {

    // Initialized in onCreateEngine(). Will be injected via Koin in Phase 1.3.2.
    private lateinit var lifecycleManager: MoriLifecycleManager

    override fun onCreateEngine(): Engine {
        return MoriEngineImpl()
    }

    private inner class MoriEngineImpl : Engine() {

        private val moriEngine = MoriEngine(this)

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            
            if (::lifecycleManager.isInitialized) {
                if (visible) {
                    lifecycleManager.onStart()
                } else {
                    lifecycleManager.onStop()
                }
            }
            
            if (visible) {
                moriEngine.onDrawFrame()
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            moriEngine.onCreate(surfaceHolder)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            moriEngine.onDrawFrame()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            
            // Ensure lifecycle is stopped when surface is destroyed
            if (::lifecycleManager.isInitialized) {
                lifecycleManager.onStop()
            }
            
            moriEngine.onDestroy()
        }
    }
}
