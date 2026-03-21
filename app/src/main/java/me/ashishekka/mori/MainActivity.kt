package me.ashishekka.mori

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.ashishekka.mori.app.components.EngineBackdrop
import me.ashishekka.mori.engine.renderer.DebugPulseRenderer
import me.ashishekka.mori.engine.renderer.LayerManager
import me.ashishekka.mori.engine.renderer.StaticFallbackRenderer
import me.ashishekka.mori.persona.lifecycle.MoriLifecycleManager
import me.ashishekka.mori.persona.state.StateManager
import me.ashishekka.mori.ui.components.MoriCard
import me.ashishekka.mori.ui.theme.MoriTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val stateManager: StateManager by inject()
    private val lifecycleManager: MoriLifecycleManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val worldState by stateManager.state.collectAsState()
            
            // Start the sensors when the Activity is active
            DisposableEffect(lifecycleManager) {
                lifecycleManager.onStart()
                onDispose {
                    lifecycleManager.onStop()
                }
            }

            MoriTheme(worldState) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black // Engine handles its own background
                ) {
                    // Use the Backdrop as the root host
                    EngineBackdrop(
                        worldState = worldState,
                        layerManager = remember { 
                            LayerManager().apply { 
                                addEffect(StaticFallbackRenderer(0xFF1A1A1A.toInt()))
                                addEffect(DebugPulseRenderer()) 
                            } 
                        }
                    ) {
                        // All content in this block will have access to LocalHazeSource
                        Box(modifier = Modifier.fillMaxSize()) {
                            
                            // GLASSMORPHIC CARD
                            MoriCard(
                                modifier = Modifier
                                    .size(width = 300.dp, height = 200.dp)
                                    .align(Alignment.Center),
                                thermalStress = worldState.energyThermalStress
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Text(
                                        text = "Mori Pulse Glass",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MoriTheme.colors.onSurface,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }

                            // UI Overlay
                            Text(
                                text = "Mori: Engine Active",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MoriTheme.colors.onSurface,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 48.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
