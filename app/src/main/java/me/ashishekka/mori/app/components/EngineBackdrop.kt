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
import me.ashishekka.mori.engine.core.models.ScaleMode
import me.ashishekka.mori.engine.renderer.DebugPulseRenderer
import me.ashishekka.mori.engine.renderer.LayerManager
import me.ashishekka.mori.persona.state.WorldState
import me.ashishekka.mori.ui.theme.MoriTheme

/**
 * A Compose-native backdrop that renders the Mori Engine directly to a Canvas.
 * This is used for in-app previews and as a source for glassmorphism blurs.
 */
@Composable
fun EngineBackdrop(
    modifier: Modifier = Modifier,
    worldState: WorldState = WorldState(),
    layerManager: LayerManager = remember { LayerManager().apply { addEffect(DebugPulseRenderer()) } },
    scaleMode: ScaleMode = ScaleMode.FIT,
    referenceWidth: Float = 1000f,
    referenceHeight: Float = 1000f
) {
    // 1. Prepare the Core Engine Orchestrator
    val ticker = remember { ComposeEngineTicker() }
    val composeCanvas = remember { ComposeEngineCanvas() }
    val renderSurface = remember { ComposeRenderSurface(composeCanvas) }
    val moriEngine = remember { 
        MoriEngine(ticker, renderSurface, layerManager).apply {
            this.targetScaleMode = scaleMode
            this.state.referenceWidth = referenceWidth
            this.state.referenceHeight = referenceHeight
        } 
    }
    
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
            // We use the Engine's own method for surface updates (Unifies Viewport Math)
            moriEngine.onSurfaceChanged(width.toInt(), height.toInt(), density)
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
