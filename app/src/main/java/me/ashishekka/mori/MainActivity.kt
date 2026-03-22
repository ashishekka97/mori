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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.ashishekka.mori.app.components.EngineBackdrop
import me.ashishekka.mori.engine.renderer.DebugPulseRenderer
import me.ashishekka.mori.engine.renderer.LayerManager
import me.ashishekka.mori.engine.renderer.StaticFallbackRenderer
import me.ashishekka.mori.persona.lifecycle.MoriLifecycleManager
import me.ashishekka.mori.persona.state.StateManager
import me.ashishekka.mori.ui.components.MetricGraph
import me.ashishekka.mori.ui.components.MoriButton
import me.ashishekka.mori.ui.components.MoriCard
import me.ashishekka.mori.ui.components.MoriSlider
import me.ashishekka.mori.ui.components.PulseToggle
import me.ashishekka.mori.ui.gallery.ComponentGallery
import me.ashishekka.mori.ui.theme.MoriTheme
import org.koin.android.ext.android.inject
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    private val stateManager: StateManager by inject()
    private val lifecycleManager: MoriLifecycleManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val worldState by stateManager.state.collectAsState()
            var showGallery by remember { mutableStateOf(false) }
            
            DisposableEffect(lifecycleManager) {
                lifecycleManager.onStart()
                onDispose {
                    lifecycleManager.onStop()
                }
            }

            if (showGallery) {
                Box(modifier = Modifier.fillMaxSize()) {
                    ComponentGallery(
                        modifier = Modifier.fillMaxSize()
                    )
                    MoriButton(
                        onClick = { showGallery = false },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(24.dp)
                    ) {
                        Text("BACK", color = MoriTheme.colors.onSurface, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                DashboardScreen(
                    worldState = worldState,
                    onOpenGallery = { showGallery = true }
                )
            }
        }
    }

    @Composable
    private fun DashboardScreen(
        worldState: me.ashishekka.mori.persona.state.WorldState,
        onOpenGallery: () -> Unit
    ) {
        var pulseEnabled by remember { mutableStateOf(true) }
        var dummyIntensity by remember { mutableFloatStateOf(0.7f) }
        val dummyTrend = remember { listOf(0.2f, 0.5f, 0.4f, 0.8f, 0.3f, 0.9f, 0.6f, 0.4f, 0.2f, 0.5f) }

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
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(64.dp))

                        // 1. SYSTEM STATUS CARD
                        MoriCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                                Text(
                                    text = "ENVIRONMENT",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = MoriTheme.colors.accent
                                )
                                
                                MetricGraph(
                                    data = dummyTrend,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    StatusRow("Battery", "${(worldState.energyBatteryLevel * 100).roundToInt()}%")
                                    StatusRow("Sun Altitude", String.format("%.2f", worldState.chronosSunAltitude))
                                    StatusRow("Thermal Stress", String.format("%.2f", worldState.energyThermalStress))
                                }
                            }
                        }

                        // 2. INTERACTIVE ROW
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            MoriCard(modifier = Modifier.weight(1f)) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("PULSE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MoriTheme.colors.onSurface.copy(alpha = 0.6f))
                                    PulseToggle(
                                        checked = pulseEnabled,
                                        onCheckedChange = { pulseEnabled = it },
                                        thermalStress = worldState.energyThermalStress
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                MoriButton(
                                    onClick = onOpenGallery,
                                    modifier = Modifier.fillMaxWidth(),
                                    thermalStress = worldState.energyThermalStress
                                ) {
                                    Text("GALLERY", color = MoriTheme.colors.onSurface, fontWeight = FontWeight.Bold)
                                }
                                
                                MoriButton(
                                    onClick = { /* Action */ },
                                    modifier = Modifier.fillMaxWidth(),
                                    thermalStress = worldState.energyThermalStress
                                ) {
                                    Text("SYNC", color = MoriTheme.colors.onSurface, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // 3. INTENSITY CARD
                        MoriCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("INTENSITY", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MoriTheme.colors.onSurface.copy(alpha = 0.6f))
                                MoriSlider(
                                    value = dummyIntensity,
                                    onValueChange = { dummyIntensity = it },
                                    thermalStress = worldState.energyThermalStress
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "MORI GLASS SYSTEM v1.2",
                            style = MaterialTheme.typography.labelSmall,
                            color = MoriTheme.colors.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun StatusRow(label: String, value: String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MoriTheme.colors.onSurface.copy(alpha = 0.7f))
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MoriTheme.colors.onSurface, fontWeight = FontWeight.Medium)
        }
    }
}
