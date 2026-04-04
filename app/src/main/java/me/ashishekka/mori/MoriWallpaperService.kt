package me.ashishekka.mori

import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.ashishekka.mori.app.WallpaperFactory
import me.ashishekka.mori.bridge.metrics.MetricCalculator
import me.ashishekka.mori.bridge.sync.StateSynchronizer
import me.ashishekka.mori.engine.core.MoriEngine
import me.ashishekka.mori.engine.core.MoriWallpaper
import me.ashishekka.mori.engine.core.interfaces.AssetRegistry
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

    private var activeEngine: MoriEngineImpl? = null

    override fun onCreateEngine(): Engine {
        val engine = MoriEngineImpl()
        activeEngine = engine
        return engine
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        activeEngine?.onConfigurationChanged(newConfig)
    }

    private inner class MoriEngineImpl : Engine() {

        private val ticker: EngineTicker by inject()
        private val assetRegistry: AssetRegistry by inject()
        private val renderSurface: RenderSurface by inject { parametersOf(this) }
        private val moriEngine: MoriEngine by inject { parametersOf(ticker, renderSurface, assetRegistry) }
        private val stateSynchronizer: StateSynchronizer by inject { parametersOf(engineScope, moriEngine) }

        private var isLifecycleStarted = false
        private var loadJob: Job? = null

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            
            if (visible) {
                if (!isLifecycleStarted) {
                    lifecycleManager.onStart()
                    isLifecycleStarted = true
                }
                
                loadJob?.cancel()
                // UNIFIED: Use the formal Wallpaper Spec
                loadJob = engineScope.launch {
                    val wallpaper = wallpaperFactory.loadWallpaper("childhood_canvas")
                    // CRITICAL: setWallpaper MUST be on the Main thread to avoid race conditions with onDrawFrame
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        moriEngine.setWallpaper(wallpaper)
                        
                        // Start engine only AFTER wallpaper is set
                        moriEngine.start()
                        stateSynchronizer.start()
                    }
                }
            } else {
                loadJob?.cancel()
                loadJob = null
                
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
            updateEngineMetrics(width, height)
            moriEngine.onDrawFrame()
        }

        private fun updateEngineMetrics(width: Int, height: Int) {
            val density = resources.displayMetrics.density

            // Standardize viewport metrics to match Mori DSL Spec (1000x1000)
            val refW = 1000f
            val refH = 1000f
            val scaleMode = ScaleMode.FIT

            moriEngine.targetScaleMode = scaleMode
            moriEngine.state.referenceWidth = refW
            moriEngine.state.referenceHeight = refH

            moriEngine.onSurfaceChanged(width, height, density)
            metricCalculator.updateMetrics(width, height, density)
            stateSynchronizer.updateViewport(refW, refH, scaleMode)
            
            // Sync platform facts for orientation
            val isLandscape = width > height
            moriEngine.state.setFieldValue(me.ashishekka.mori.engine.core.MoriEngineStateIndices.FACT_IS_LANDSCAPE, if (isLandscape) 1f else 0f)
            moriEngine.state.setFieldValue(me.ashishekka.mori.engine.core.MoriEngineStateIndices.FACT_ASPECT_RATIO, height.toFloat() / width.toFloat())
            moriEngine.state.setFieldValue(me.ashishekka.mori.engine.core.MoriEngineStateIndices.FACT_FIELD_RATIO, width.toFloat() / height.toFloat())
        }

        fun onConfigurationChanged(newConfig: android.content.res.Configuration?) {
            // CRITICAL: Immediately update metrics on orientation change to prevent "stale" framing
            if (isVisible) {
                val width = surfaceHolder.surfaceFrame.width()
                val height = surfaceHolder.surfaceFrame.height()
                if (width > 0 && height > 0) {
                    updateEngineMetrics(width, height)
                    moriEngine.onDrawFrame()
                }
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            if (activeEngine == this) {
                activeEngine = null
            }
            
            loadJob?.cancel()
            loadJob = null
            
            if (isLifecycleStarted) {
                lifecycleManager.onStop()
                isLifecycleStarted = false
            }
            stateSynchronizer.stop()
            moriEngine.onDestroy()
        }
    }
}
