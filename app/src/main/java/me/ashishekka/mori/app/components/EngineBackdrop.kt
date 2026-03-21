package me.ashishekka.mori.app.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import me.ashishekka.mori.engine.core.MoriEngine
import me.ashishekka.mori.engine.core.models.ScaleMode
import me.ashishekka.mori.engine.renderer.DebugPulseRenderer
import me.ashishekka.mori.engine.renderer.LayerManager
import me.ashishekka.mori.persona.state.WorldState
import me.ashishekka.mori.ui.components.HazeSource
import me.ashishekka.mori.ui.components.LocalHazeSource
import me.ashishekka.mori.ui.theme.MoriTheme

/**
 * A Compose-native backdrop that renders the Mori Engine directly to a Canvas.
 * This component captures its output into a GraphicsLayer and shares it
 * via [LocalHazeSource] for backdrop blur effects.
 * 
 * @param content The UI content to be displayed on top of the backdrop.
 */
@Composable
fun EngineBackdrop(
    modifier: Modifier = Modifier,
    worldState: WorldState = WorldState(),
    layerManager: LayerManager = remember { LayerManager().apply { addEffect(DebugPulseRenderer()) } },
    scaleMode: ScaleMode = ScaleMode.FIT,
    referenceWidth: Float = 1000f,
    referenceHeight: Float = 1000f,
    content: @Composable () -> Unit = {}
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
    
    // 2. Prepare the Capture Layer (The source for glassmorphism)
    val graphicsLayer = rememberGraphicsLayer()
    val density = LocalDensity.current.density
    
    // Engine State extraction for UI Theme sync
    var frameTime by remember { mutableLongStateOf(0L) }
    val dominantAccent = remember(moriEngine.state.dominantAccentColor) { 
        Color(moriEngine.state.dominantAccentColor) 
    }

    LaunchedEffect(worldState) {
        syncWorldToEngine(worldState, moriEngine.state)
    }

    LaunchedEffect(Unit) {
        moriEngine.start()
        while (true) {
            withFrameNanos { time ->
                frameTime = time
                ticker.tick(time)
            }
        }
    }

    // 3. Provide the layer to the hierarchy
    CompositionLocalProvider(
        LocalHazeSource provides HazeSource(graphicsLayer)
    ) {
        // 4. The Muscle (Compose Surface)
        Canvas(
            modifier = modifier.fillMaxSize()
        ) {
            @Suppress("UNUSED_VARIABLE")
            val invalidate = frameTime

            val width = size.width
            val height = size.height
            
            if (moriEngine.state.surfaceWidth != width.toInt() || moriEngine.state.surfaceHeight != height.toInt()) {
                moriEngine.onSurfaceChanged(width.toInt(), height.toInt(), density)
            }

            // Record the Engine output into the shared GraphicsLayer
            graphicsLayer.record {
                composeCanvas.drawScope = this
                moriEngine.onDrawFrame(frameTime)
                composeCanvas.drawScope = null
            }

            // Draw the captured layer onto the main Canvas
            drawLayer(graphicsLayer)
        }

        // 5. Inject the Living Palette into the UI content
        // We re-wrap the content in a MoriTheme that uses the engine's accent
        MoriTheme(worldState = worldState, accentOverride = dominantAccent) {
            content()
        }
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
