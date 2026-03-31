package me.ashishekka.mori

import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import kotlinx.coroutines.CoroutineScope
import me.ashishekka.mori.app.WallpaperFactory
import me.ashishekka.mori.bridge.metrics.MetricCalculator
import me.ashishekka.mori.bridge.sync.StateSynchronizer
import me.ashishekka.mori.engine.core.MoriEngine
import me.ashishekka.mori.engine.core.MoriWallpaper
import me.ashishekka.mori.engine.core.interfaces.EngineTicker
import me.ashishekka.mori.engine.core.interfaces.RenderSurface
import me.ashishekka.mori.engine.core.models.ScaleMode
import me.ashishekka.mori.persona.lifecycle.MoriLifecycleManager
import me.ashishekka.mori.persona.state.StateManager
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

/**
 * The system entry point for the Mori Live Wallpaper.
 */
class MoriWallpaperService : WallpaperService() {

    private val lifecycleManager: MoriLifecycleManager by inject()
    private val stateManager: StateManager by inject()
    private val metricCalculator: MetricCalculator by inject()
    private val wallpaperFactory: WallpaperFactory by inject()
    private val engineScope: CoroutineScope by inject(named("EngineScope"))

    override fun onCreateEngine(): Engine {
        return MoriEngineImpl()
    }

    private inner class MoriEngineImpl : Engine() {

        private val ticker: EngineTicker by inject()
        private val renderSurface: RenderSurface by inject { parametersOf(this) }
        private val moriEngine: MoriEngine by inject { parametersOf(ticker, renderSurface) }
        private val stateSynchronizer: StateSynchronizer by inject { parametersOf(engineScope, moriEngine) }

        private var isLifecycleStarted = false

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            
            if (visible) {
                if (!isLifecycleStarted) {
                    lifecycleManager.onStart()
                    isLifecycleStarted = true
                }
                
                // UNIFIED: Use the formal Wallpaper Spec
                moriEngine.setWallpaper(wallpaperFactory.createDebugPrismWallpaper())
                
                moriEngine.start()
                stateSynchronizer.start()
            } else {
                if (isLifecycleStarted) {
                    lifecycleManager.onStop()
                    isLifecycleStarted = false
                }
                moriEngine.stop()
                stateSynchronizer.stop()
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            val density = resources.displayMetrics.density
            
            moriEngine.targetScaleMode = ScaleMode.FIT
            moriEngine.state.referenceWidth = 1000f
            moriEngine.state.referenceHeight = 1000f
            
            moriEngine.onSurfaceChanged(width, height, density)
            metricCalculator.updateMetrics(width, height, density)
            moriEngine.onDrawFrame()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            
            if (isLifecycleStarted) {
                lifecycleManager.onStop()
                isLifecycleStarted = false
            }
            stateSynchronizer.stop()
            moriEngine.onDestroy()
        }
    }
}
