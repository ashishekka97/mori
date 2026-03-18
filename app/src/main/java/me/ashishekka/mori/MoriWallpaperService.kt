package me.ashishekka.mori

import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.ashishekka.mori.engine.ChoreographerTicker
import me.ashishekka.mori.engine.SurfaceHolderRenderSurface
import me.ashishekka.mori.engine.core.MoriEngine
import me.ashishekka.mori.persona.lifecycle.MoriLifecycleManager
import me.ashishekka.mori.persona.state.StateManager
import org.koin.android.ext.android.inject

/**
 * The system entry point for the Mori Live Wallpaper.
 * Located in the :app module to orchestrate the [MoriLifecycleManager]
 * and the [MoriEngine].
 */
class MoriWallpaperService : WallpaperService() {

    private val lifecycleManager: MoriLifecycleManager by inject()
    private val stateManager: StateManager by inject()

    override fun onCreateEngine(): Engine {
        return MoriEngineImpl()
    }

    private inner class MoriEngineImpl : Engine() {

        private val moriEngine = MoriEngine(
            ticker = ChoreographerTicker(),
            renderSurface = SurfaceHolderRenderSurface(this)
        )
        private val engineScope = CoroutineScope(Dispatchers.Main + Job())
        private var stateCollectionJob: Job? = null

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            
            if (visible) {
                lifecycleManager.onStart()
                moriEngine.start()
                startStateCollection()
            } else {
                lifecycleManager.onStop()
                moriEngine.stop()
                stopStateCollection()
            }
        }

        private fun startStateCollection() {
            if (stateCollectionJob?.isActive == true) return
            stateCollectionJob = engineScope.launch {
                stateManager.state.collect { state ->
                    // 2.1.4: Orchestrator triggers on-demand rendering on state change
                    moriEngine.requestFrame()
                }
            }
        }

        private fun stopStateCollection() {
            stateCollectionJob?.cancel()
            stateCollectionJob = null
        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            // Initial frame when surface is created or changed
            moriEngine.onDrawFrame()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            
            lifecycleManager.onStop()
            stopStateCollection()
            moriEngine.onDestroy()
        }
    }
}
