package me.ashishekka.mori

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import me.ashishekka.mori.ui.components.MoriButton
import me.ashishekka.mori.ui.components.MoriCard
import me.ashishekka.mori.ui.components.MoriSlider
import me.ashishekka.mori.ui.components.PulseToggle
import me.ashishekka.mori.ui.theme.MoriTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val stateManager: StateManager by inject()
    private val lifecycleManager: MoriLifecycleManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val worldState by stateManager.state.collectAsState()
            var toggleChecked by remember { mutableStateOf(true) }
            var sliderValue by remember { mutableFloatStateOf(0.5f) }
            
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
                    color = Color.Black
                ) {
                    EngineBackdrop(
                        worldState = worldState,
                        layerManager = remember { 
                            LayerManager().apply { 
                                addEffect(StaticFallbackRenderer(0xFF1A1A1A.toInt()))
                                addEffect(DebugPulseRenderer()) 
                            } 
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(48.dp))

                            // 1. GLASS CARD
                            MoriCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                thermalStress = worldState.energyThermalStress
                            ) {
                                Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
                                    Text(
                                        text = "Atmosphere Controls",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MoriTheme.colors.onSurface
                                    )
                                    Text(
                                        text = "Synced Glassmorphism",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MoriTheme.colors.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            // 2. TOGGLE & BUTTON ROW
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                MoriCard(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Pulse", color = MoriTheme.colors.onSurface)
                                        PulseToggle(
                                            checked = toggleChecked,
                                            onCheckedChange = { toggleChecked = it },
                                            thermalStress = worldState.energyThermalStress
                                        )
                                    }
                                }

                                MoriButton(
                                    onClick = { /* Action */ },
                                    modifier = Modifier.weight(1f),
                                    thermalStress = worldState.energyThermalStress
                                ) {
                                    Text("Action", color = MoriTheme.colors.onSurface)
                                }
                            }

                            // 3. SLIDER CARD
                            MoriCard(modifier = Modifier.fillMaxWidth()) {
                                Column {
                                    Text("Intensity", color = MoriTheme.colors.onSurface)
                                    MoriSlider(
                                        value = sliderValue,
                                        onValueChange = { sliderValue = it },
                                        thermalStress = worldState.energyThermalStress
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // UI Overlay
                            Text(
                                text = "Mori: Engine Active",
                                style = MaterialTheme.typography.labelLarge,
                                color = MoriTheme.colors.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.padding(bottom = 24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
