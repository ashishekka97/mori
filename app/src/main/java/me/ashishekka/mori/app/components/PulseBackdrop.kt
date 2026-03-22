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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import me.ashishekka.mori.bridge.sync.StateHandover
import me.ashishekka.mori.engine.core.MoriEngine
import me.ashishekka.mori.engine.core.MoriWallpaper
import me.ashishekka.mori.engine.core.models.ScaleMode
import me.ashishekka.mori.engine.renderer.LayerManager
import me.ashishekka.mori.persona.state.WorldState
import me.ashishekka.mori.ui.components.LocalPulseHazeSource
import me.ashishekka.mori.ui.components.PulseHazeSource
import me.ashishekka.mori.ui.theme.PulseColors
import me.ashishekka.mori.ui.theme.PulseTheme

@Composable
fun PulseBackdrop(
    modifier: Modifier = Modifier,
    worldState: WorldState = WorldState(),
    wallpaper: MoriWallpaper = remember { MoriWallpaper.createDebugWallpaper() },
    scaleMode: ScaleMode = ScaleMode.FIT,
    referenceWidth: Float = 1000f,
    referenceHeight: Float = 1100f,
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
            this.setWallpaper(wallpaper)
        } 
    }
    
    val graphicsLayer = rememberGraphicsLayer()
    val density = LocalDensity.current.density
    
    var frameTick by remember { mutableIntStateOf(0) }
    var lastNanos by remember { mutableStateOf(0L) }
    
    var enginePalette by remember { 
        mutableStateOf(
            PulseColors(
                accent = Color.Transparent, surface = Color.Transparent, 
                onSurface = Color.Transparent, isDark = false
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

    // UI INTEGRITY SMOKE TEST:
    // This bypasses the engine and forces the UI to cycle colors.
    // If this works, the UI recomposition is correct.
    // If it fails, the Compose UI layer has a flaw.
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { time ->
                lastNanos = time
                frameTick++
                ticker.tick(time)
                
                val seconds = (time / 1_000_000_000L)
                val testPalette = when (seconds % 3) {
                    0L -> PulseColors(accent = Color.Magenta, surface = Color.Red, onSurface = Color.White, isDark = true)
                    1L -> PulseColors(accent = Color.Yellow, surface = Color.Green, onSurface = Color.Black, isDark = false)
                    else -> PulseColors(accent = Color.Cyan, surface = Color.Blue, onSurface = Color.White, isDark = true)
                }
                enginePalette = testPalette
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
