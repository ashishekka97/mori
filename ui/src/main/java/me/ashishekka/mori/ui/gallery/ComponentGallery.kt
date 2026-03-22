package me.ashishekka.mori.ui.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.ashishekka.mori.persona.state.WorldState
import me.ashishekka.mori.ui.components.MetricGraph
import me.ashishekka.mori.ui.components.MoriButton
import me.ashishekka.mori.ui.components.MoriCard
import me.ashishekka.mori.ui.components.MoriSlider
import me.ashishekka.mori.ui.components.PulseToggle
import me.ashishekka.mori.ui.theme.MoriTheme

/**
 * A dedicated screen to verify and showcase all components of the Mori Pulse Design System.
 */
@Composable
fun ComponentGallery(
    modifier: Modifier = Modifier
) {
    // Gallery State Simulator
    var sunAltitude by remember { mutableFloatStateOf(0.5f) }
    var thermalStress by remember { mutableFloatStateOf(0f) }
    
    val simulatedState = remember(sunAltitude, thermalStress) {
        WorldState(
            chronosSunAltitude = sunAltitude,
            energyThermalStress = thermalStress
        )
    }

    MoriTheme(simulatedState) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(if (simulatedState.chronosSunAltitude > 0) Color(0xFFF5F5F5) else Color(0xFF121212))
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Pulse Design Gallery",
                style = MaterialTheme.typography.headlineMedium,
                color = MoriTheme.colors.onSurface
            )

            // SECTION: STATE SIMULATOR
            GallerySection(title = "State Simulator") {
                MoriCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        SimulatorControl(
                            label = "Sun Altitude",
                            value = sunAltitude,
                            onValueChange = { sunAltitude = it },
                            range = -1f..1f
                        )
                        SimulatorControl(
                            label = "Thermal Stress",
                            value = thermalStress,
                            onValueChange = { thermalStress = it },
                            range = 0f..1f
                        )
                    }
                }
            }

            // SECTION: TYPOGRAPHY
            GallerySection(title = "Typography") {
                MoriCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Headline Large", style = MaterialTheme.typography.headlineLarge, color = MoriTheme.colors.onSurface)
                        Text("Title Medium", style = MaterialTheme.typography.titleMedium, color = MoriTheme.colors.onSurface)
                        Text("Body Large", style = MaterialTheme.typography.bodyLarge, color = MoriTheme.colors.onSurface)
                        Text("Label Small", style = MaterialTheme.typography.labelSmall, color = MoriTheme.colors.accent)
                    }
                }
            }

            // SECTION: INTERACTIVE
            GallerySection(title = "Interactive Controls") {
                var toggleChecked by remember { mutableStateOf(true) }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MoriCard(modifier = Modifier.weight(1f)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Pulse", style = MaterialTheme.typography.labelSmall, color = MoriTheme.colors.onSurface)
                            Spacer(Modifier.height(8.dp))
                            PulseToggle(
                                checked = toggleChecked,
                                onCheckedChange = { toggleChecked = it },
                                thermalStress = thermalStress
                            )
                        }
                    }
                    MoriButton(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        thermalStress = thermalStress
                    ) {
                        Text("Action", color = MoriTheme.colors.onSurface)
                    }
                }
            }

            // SECTION: VISUALIZERS
            GallerySection(title = "Data Visualizers") {
                val dummyTrend = remember { listOf(0.2f, 0.5f, 0.4f, 0.8f, 0.3f, 0.9f, 0.6f) }
                MoriCard {
                    MetricGraph(
                        data = dummyTrend,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )
                }
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun GallerySection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MoriTheme.colors.accent.copy(alpha = 0.8f)
        )
        content()
    }
}

@Composable
private fun SimulatorControl(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MoriTheme.colors.onSurface)
            Text(text = String.format("%.2f", value), style = MaterialTheme.typography.labelMedium, color = MoriTheme.colors.onSurface)
        }
        MoriSlider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewComponentGallery() {
    ComponentGallery()
}
