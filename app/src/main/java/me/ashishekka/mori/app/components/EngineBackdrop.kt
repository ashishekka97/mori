package me.ashishekka.mori.app.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import me.ashishekka.mori.engine.core.MoriEngine
import me.ashishekka.mori.engine.renderer.DebugPulseRenderer
import me.ashishekka.mori.engine.renderer.LayerManager
import me.ashishekka.mori.persona.state.WorldState
import me.ashishekka.mori.ui.theme.MoriTheme
import kotlin.math.max
import kotlin.math.min

/**
 * Strategy for scaling a reference canvas to the actual device screen.
 */
enum class EngineScaleMode {
    FIT, FILL
}

/**
 * A Compose-native backdrop that renders the Mori Engine directly to a Canvas.
 * This is used for in-app previews and as a source for glassmorphism blurs.
 */
@Composable
fun EngineBackdrop(
    modifier: Modifier = Modifier,
    worldState: WorldState = WorldState(),
    layerManager: LayerManager = remember { LayerManager().apply { addEffect(DebugPulseRenderer()) } },
    scaleMode: EngineScaleMode = EngineScaleMode.FIT,
    referenceWidth: Float = 1000f,
    referenceHeight: Float = 1000f
) {
    // 1. Prepare the Core Engine Orchestrator
    val ticker = remember { ComposeEngineTicker() }
    val composeCanvas = remember { ComposeEngineCanvas() }
    val renderSurface = remember { ComposeRenderSurface(composeCanvas) }
    val moriEngine = remember { MoriEngine(ticker, renderSurface, layerManager) }
    
    val density = LocalDensity.current.density
    
    // This state drives the recomposition loop
    var frameTime by remember { mutableLongStateOf(0L) }

    // 2. State Sync (Brain to Muscle)
    LaunchedEffect(worldState) {
        syncWorldToEngine(worldState, moriEngine.state)
    }

    // 3. The Animation Loop (Ticker to Engine)
    LaunchedEffect(Unit) {
        moriEngine.start()
        while (true) {
            withFrameNanos { time ->
                frameTime = time
                // The Ticker "invokes" the engine onDrawFrame logic
                ticker.tick(time)
            }
        }
    }

    // 4. The Muscle (Compose Surface)
    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        // Accessing frameTime here ensures the Canvas is re-drawn every frame
        @Suppress("UNUSED_VARIABLE")
        val invalidate = frameTime

        // Update Surface Metrics if they changed
        val width = size.width
        val height = size.height
        
        if (moriEngine.state.surfaceWidth != width.toInt() || moriEngine.state.surfaceHeight != height.toInt()) {
            val scaleX = width / referenceWidth
            val scaleY = height / referenceHeight
            val scale = when (scaleMode) {
                EngineScaleMode.FIT -> min(scaleX, scaleY)
                EngineScaleMode.FILL -> max(scaleX, scaleY)
            }
            
            // We use the Engine's own method for surface updates
            moriEngine.onSurfaceChanged(width.toInt(), height.toInt(), density)
            
            // Manual viewport calculation (Shared logic from Bridge)
            moriEngine.state.viewportReferenceScale = scale
            moriEngine.state.viewportSafeWidth = referenceWidth * scale
            moriEngine.state.viewportSafeHeight = referenceHeight * scale
            moriEngine.state.viewportSafeX = (width - moriEngine.state.viewportSafeWidth) / 2f
            moriEngine.state.viewportSafeY = (height - moriEngine.state.viewportSafeHeight) / 2f
        }

        // EXECUTE: Bridging the DrawScope to the Engine
        composeCanvas.drawScope = this
        moriEngine.onDrawFrame(frameTime) // Orchestrator handles updateAndDraw logic
        composeCanvas.drawScope = null
    }
}

/**
 * Manual field-by-field sync.
 */
private fun syncWorldToEngine(from: WorldState, to: me.ashishekka.mori.engine.core.MoriEngineState) {
    to.chronosTimeProgress = from.chronosTimeProgress
    to.chronosSunAltitude = from.chronosSunAltitude
    to.chronosMoonPhase = from.chronosMoonPhase
    to.chronosSeasonProgress = from.chronosSeasonProgress
    to.chronosIsWeekend = from.chronosIsWeekend

    to.vitalityStepsProgress = from.vitalityStepsProgress
    to.vitalityActivityIntensity = from.vitalityActivityIntensity
    to.vitalitySleepClarity = from.vitalitySleepClarity
    to.vitalityStandGoalProgress = from.vitalityStandGoalProgress

    to.zenDigitalCongestion = from.zenDigitalCongestion
    to.zenSocialNoise = from.zenSocialNoise
    to.zenContextSwitching = from.zenContextSwitching
    to.zenIsDndActive = from.zenIsDndActive
    to.zenLastInteractionAge = from.zenLastInteractionAge

    to.energyBatteryLevel = from.energyBatteryLevel
    to.energyIsCharging = from.energyIsCharging
    to.energyThermalStress = from.energyThermalStress

    to.atmosLightLevel = from.atmosLightLevel
    to.atmosIsPocketed = from.atmosIsPocketed
}

@Preview(showBackground = true)
@Composable
fun PreviewEngineBackdrop() {
    MoriTheme {
        EngineBackdrop(
            worldState = WorldState(
                energyBatteryLevel = 0.8f,
                chronosSunAltitude = 0.5f
            )
        )
    }
}
