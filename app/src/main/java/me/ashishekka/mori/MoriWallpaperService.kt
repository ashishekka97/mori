package me.ashishekka.mori

import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import kotlinx.coroutines.CoroutineScope
import me.ashishekka.mori.bridge.metrics.MetricCalculator
import me.ashishekka.mori.bridge.sync.StateSynchronizer
import me.ashishekka.mori.engine.core.MoriEngine
import me.ashishekka.mori.engine.core.interfaces.EngineTicker
import me.ashishekka.mori.engine.core.interfaces.RenderSurface
import me.ashishekka.mori.engine.core.models.ScaleMode
import me.ashishekka.mori.engine.renderer.DebugPulseRenderer
import me.ashishekka.mori.engine.renderer.StaticFallbackRenderer
import me.ashishekka.mori.persona.lifecycle.MoriLifecycleManager
import me.ashishekka.mori.persona.state.StateManager
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

/**
 * The system entry point for the Mori Live Wallpaper.
 * Located in the :app module to orchestrate the [MoriLifecycleManager]
 * and the [MoriEngine].
 */
class MoriWallpaperService : WallpaperService() {

    private val lifecycleManager: MoriLifecycleManager by inject()
    private val stateManager: StateManager by inject()
    private val metricCalculator: MetricCalculator by inject()
    private val engineScope: CoroutineScope by inject(named("EngineScope"))

    override fun onCreateEngine(): Engine {
        return MoriEngineImpl()
    }

    private inner class MoriEngineImpl : Engine() {

        private val ticker: EngineTicker by inject()
        private val renderSurface: RenderSurface by inject { parametersOf(this) }
        private val moriEngine: MoriEngine by inject { parametersOf(ticker, renderSurface) }
        private val stateSynchronizer: StateSynchronizer by inject { parametersOf(engineScope, moriEngine) }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            
            if (visible) {
                lifecycleManager.onStart()
                
                // Phase 3 Smoke Test: Dual-layer verification
                // 1. Physical Background (Full Screen)
                moriEngine.addEffect(StaticFallbackRenderer(0xFF1A1A1A.toInt()))
                // 2. Safe Area Foreground (Calculated by Engine Internal Math)
                moriEngine.addEffect(DebugPulseRenderer())
                
                moriEngine.start()
                stateSynchronizer.start()
            } else {
                lifecycleManager.onStop()
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
            
            // Initializing Engine Configuration
            moriEngine.targetScaleMode = ScaleMode.FIT
            moriEngine.state.referenceWidth = 1000f
            moriEngine.state.referenceHeight = 1000f
            
            // UNIFIED: Use the engine's internal math for the viewport
            moriEngine.onSurfaceChanged(width, height, density)
            
            // Keep the bridge informed for metrics (optional but good for future sensors)
            metricCalculator.updateMetrics(width, height, density)
            
            // Initial frame
            moriEngine.onDrawFrame()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            
            lifecycleManager.onStop()
            stateSynchronizer.stop()
            moriEngine.onDestroy()
        }
    }
}
