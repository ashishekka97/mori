package me.ashishekka.mori.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import me.ashishekka.mori.engine.core.MoriEngineState
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
    scaleMode: EngineScaleMode = EngineScaleMode.FILL,
    referenceWidth: Float = 1000f,
    referenceHeight: Float = 1000f
) {
    val engineState = remember { MoriEngineState() }
    val composeCanvas = remember { ComposeEngineCanvas() }
    val density = LocalDensity.current.density

    // Update the engine state whenever worldState or surface metrics change
    LaunchedEffect(worldState, scaleMode, referenceWidth, referenceHeight) {
        syncWorldToEngine(worldState, engineState)
    }

    // The Animation Loop (Driven by the device refresh rate)
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { _ ->
                // In Compose, we don't need to manually trigger draw here,
                // we just need the worldState updates to flow.
                // Recomposition will handle the rest if we use State.
                // However, the Engine expects a tick. 
                // For this component, we'll let the Canvas draw pass be the tick.
            }
        }
    }

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        // 1. Update Surface Metrics
        val width = size.width
        val height = size.height
        
        if (engineState.surfaceWidth != width.toInt() || engineState.surfaceHeight != height.toInt()) {
            engineState.surfaceWidth = width.toInt()
            engineState.surfaceHeight = height.toInt()
            engineState.surfaceDensity = density
            
            // Recalculate Viewport (Geometry Handover)
            val scaleX = width / referenceWidth
            val scaleY = height / referenceHeight
            val scale = when (scaleMode) {
                EngineScaleMode.FIT -> min(scaleX, scaleY)
                EngineScaleMode.FILL -> max(scaleX, scaleY)
            }
            
            engineState.viewportReferenceScale = scale
            engineState.viewportSafeWidth = referenceWidth * scale
            engineState.viewportSafeHeight = referenceHeight * scale
            engineState.viewportSafeX = (width - engineState.viewportSafeWidth) / 2f
            engineState.viewportSafeY = (height - engineState.viewportSafeHeight) / 2f
            
            layerManager.onSurfaceChanged(width.toInt(), height.toInt(), density)
        }

        // 2. Draw the frame
        composeCanvas.drawScope = this
        layerManager.updateAndDraw(engineState, composeCanvas)
        composeCanvas.drawScope = null
    }
}

/**
 * Manual field-by-field sync to maintain the zero-allocation mandate.
 */
private fun syncWorldToEngine(from: WorldState, to: MoriEngineState) {
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
                chronosSunAltitude = 0.5f // Golden hour
            )
        )
    }
}
