package me.ashishekka.mori.app.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.IntSize
import me.ashishekka.mori.bridge.sync.StateHandover
import me.ashishekka.mori.engine.core.MoriEngine
import me.ashishekka.mori.engine.core.MoriWallpaper
import me.ashishekka.mori.engine.core.models.ScaleMode
import me.ashishekka.mori.engine.renderer.LayerManager
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
    wallpaper: MoriWallpaper = remember { MoriWallpaper.createDebugWallpaper() },
    scaleMode: ScaleMode = ScaleMode.FIT,
    referenceWidth: Float = 1000f,
    referenceHeight: Float = 1000f,
    content: @Composable () -> Unit = {}
) {
    val ticker = remember { ComposeEngineTicker() }
    val composeCanvas = remember { ComposeEngineCanvas() }
    val renderSurface = remember { ComposeRenderSurface(composeCanvas) }
    val moriEngine = remember { 
        MoriEngine(ticker, renderSurface, LayerManager()).apply {
            this.targetScaleMode = scaleMode
            this.state.referenceWidth = referenceWidth
            this.state.referenceHeight = referenceHeight
            this.setWallpaper(wallpaper) // Initialize with the provided spec
        } 
    }
    
    val graphicsLayer = rememberGraphicsLayer()
    val density = LocalDensity.current.density
    
    var frameTick by remember { mutableIntStateOf(0) }
    var lastNanos by remember { mutableStateOf(0L) }
    
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
                lastNanos = time
                frameTick++
                ticker.tick(time)
                
                val engineState = moriEngine.state
                val newAccent = Color(engineState.dominantAccentColor)
                val newIsDark = engineState.isDarkState

                if (enginePalette.accent != newAccent || enginePalette.isDark != newIsDark) {
                    enginePalette = PulseColors(
                        accent = newAccent,
                        surface = Color(engineState.dominantSurfaceColor),
                        onSurface = Color(engineState.dominantOnSurfaceColor),
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
            val tick = frameTick

            val width = size.width
            val height = size.height
            val intSize = IntSize(width.toInt(), height.toInt())

            if (moriEngine.state.surfaceWidth != width.toInt() || moriEngine.state.surfaceHeight != height.toInt()) {
                moriEngine.onSurfaceChanged(width.toInt(), height.toInt(), density)
            }

            graphicsLayer.record(intSize) {
                composeCanvas.drawScope = this
                moriEngine.onDrawFrame(lastNanos)
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
