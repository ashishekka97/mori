package me.ashishekka.mori.app.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import me.ashishekka.mori.bridge.sync.StateHandover
import me.ashishekka.mori.engine.core.MoriEngine
import me.ashishekka.mori.engine.core.models.ScaleMode
import me.ashishekka.mori.engine.renderer.DebugPulseRenderer
import me.ashishekka.mori.engine.renderer.LayerManager
import me.ashishekka.mori.engine.renderer.StaticFallbackRenderer
import me.ashishekka.mori.persona.state.WorldState
import me.ashishekka.mori.ui.components.PulseHazeSource
import me.ashishekka.mori.ui.components.LocalPulseHazeSource
import me.ashishekka.mori.ui.theme.PulseColors
import me.ashishekka.mori.ui.theme.PulseTheme

/**
 * A Compose-native backdrop that renders the Mori Engine directly to a Canvas.
 */
@Composable
fun PulseBackdrop(
    modifier: Modifier = Modifier,
    worldState: WorldState = WorldState(),
    layerManager: LayerManager = remember { LayerManager().apply { addEffect(DebugPulseRenderer()) } },
    scaleMode: ScaleMode = ScaleMode.FIT,
    referenceWidth: Float = 1000f,
    referenceHeight: Float = 1000f,
    content: @Composable () -> Unit = {}
) {
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
    
    val graphicsLayer = rememberGraphicsLayer()
    val density = LocalDensity.current.density
    
    var frameTime by remember { mutableLongStateOf(0L) }
    
    // HARDENING: Start with a stable initial palette
    var enginePalette by remember { 
        mutableStateOf(
            PulseColors(
                accent = Color(moriEngine.state.dominantAccentColor),
                surface = Color(moriEngine.state.dominantSurfaceColor),
                onSurface = Color(moriEngine.state.dominantOnSurfaceColor),
                isDark = moriEngine.state.isDarkState
            )
        )
    }

    LaunchedEffect(worldState) {
        StateHandover.sync(worldState, moriEngine.state)
    }

    DisposableEffect(Unit) {
        moriEngine.start()
        onDispose {
            moriEngine.stop()
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { time ->
                frameTime = time
                ticker.tick(time)
                
                // HARDENING: Zero-Allocation Guard. 
                // Only update the state (and allocate a new PulseColors object) 
                // if the engine palette has actually changed.
                val engineState = moriEngine.state
                val newAccent = engineState.dominantAccentColor
                val newSurface = engineState.dominantSurfaceColor
                val newOnSurface = engineState.dominantOnSurfaceColor
                val newIsDark = engineState.isDarkState

                if (enginePalette.accent.value.toLong() != newAccent.toLong().shl(32).ushr(32) || 
                    enginePalette.isDark != newIsDark) {
                    
                    enginePalette = PulseColors(
                        accent = Color(newAccent),
                        surface = Color(newSurface),
                        onSurface = Color(newOnSurface),
                        isDark = newIsDark
                    )
                }
            }
        }
    }

    CompositionLocalProvider(
        LocalPulseHazeSource provides PulseHazeSource(graphicsLayer)
    ) {
        Canvas(
            modifier = modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        ) {
            @Suppress("UNUSED_VARIABLE")
            val invalidate = frameTime

            val width = size.width
            val height = size.height
            
            if (moriEngine.state.surfaceWidth != width.toInt() || moriEngine.state.surfaceHeight != height.toInt()) {
                moriEngine.onSurfaceChanged(width.toInt(), height.toInt(), density)
            }

            graphicsLayer.record {
                composeCanvas.drawScope = this
                moriEngine.onDrawFrame(frameTime)
                composeCanvas.drawScope = null
            }

            drawLayer(graphicsLayer)
        }

        PulseTheme(worldState = worldState, paletteOverride = enginePalette) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPulseBackdrop() {
    PulseTheme {
        PulseBackdrop(
            worldState = WorldState(
                energyBatteryLevel = 0.8f,
                chronosSunAltitude = 0.5f
            )
        )
    }
}
