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
import androidx.compose.ui.unit.IntSize
import java.io.InputStream
import me.ashishekka.mori.bridge.sync.StateHandover
import me.ashishekka.mori.engine.core.MoriEngine
import me.ashishekka.mori.engine.core.MoriWallpaper
import me.ashishekka.mori.engine.core.interfaces.AssetRegistry
import me.ashishekka.mori.engine.core.models.AssetType
import me.ashishekka.mori.engine.core.models.AtlasRegion
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
    assetRegistry: AssetRegistry,
    scaleMode: ScaleMode = ScaleMode.FIT,
    referenceWidth: Float = 1000f,
    referenceHeight: Float = 1000f,
    content: @Composable () -> Unit = {}
) {
    val ticker = remember { ComposeEngineTicker() }
    val composeCanvas = remember { ComposeEngineCanvas(assetRegistry) }
    val renderSurface = remember { ComposeRenderSurface(composeCanvas) }
    val moriEngine = remember { 
        MoriEngine(ticker, renderSurface, LayerManager(), assetRegistry).apply {
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

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { time ->
                lastNanos = time
                frameTick++
                ticker.tick(time)
                
                val engineState = moriEngine.state
                val newAccent = Color(engineState.dominantAccentColor)
                val newSurface = Color(engineState.dominantSurfaceColor)
                val newOnSurface = Color(engineState.dominantOnSurfaceColor)
                val newIsDark = engineState.isDarkState

                // COMPREHENSIVE GUARD: Check all palette colors for changes.
                if (enginePalette.accent != newAccent || enginePalette.surface != newSurface ||
                    enginePalette.onSurface != newOnSurface || enginePalette.isDark != newIsDark) {
                    
                    enginePalette = PulseColors(
                        accent = newAccent,
                        surface = newSurface,
                        onSurface = newOnSurface,
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
