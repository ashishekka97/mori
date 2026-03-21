package me.ashishekka.mori.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.ashishekka.mori.persona.state.WorldState
import me.ashishekka.mori.ui.theme.MoriTheme

/**
 * A unified gallery showcasing all glassmorphic atmosphere controls.
 */
@Composable
fun AtmosphereControlsGallery(
    worldState: WorldState,
    modifier: Modifier = Modifier
) {
    var toggleChecked by remember { mutableStateOf(true) }
    var sliderValue by remember { mutableFloatStateOf(0.7f) }

    MoriTheme(worldState) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Atmosphere Suite",
                style = MaterialTheme.typography.titleLarge,
                color = MoriTheme.colors.onSurface
            )

            // 1. Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MoriButton(
                    onClick = {},
                    thermalStress = worldState.energyThermalStress,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Primary", color = MoriTheme.colors.onSurface)
                }
                MoriButton(
                    onClick = {},
                    thermalStress = worldState.energyThermalStress,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Secondary", color = MoriTheme.colors.onSurface)
                }
            }

            // 2. Toggles in a Card
            MoriCard(thermalStress = worldState.energyThermalStress) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sync Pulse", color = MoriTheme.colors.onSurface)
                    PulseToggle(
                        checked = toggleChecked,
                        onCheckedChange = { toggleChecked = it },
                        thermalStress = worldState.energyThermalStress
                    )
                }
            }

            // 3. Slider in a Card
            MoriCard(thermalStress = worldState.energyThermalStress) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Atmosphere Intensity", color = MoriTheme.colors.onSurface)
                    MoriSlider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        thermalStress = worldState.energyThermalStress
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Gallery - Golden Hour")
@Composable
fun PreviewGalleryGoldenHour() {
    AtmosphereControlsGallery(worldState = WorldState(chronosSunAltitude = 0.5f))
}

@Preview(showBackground = true, name = "Gallery - Midnight")
@Composable
fun PreviewGalleryMidnight() {
    AtmosphereControlsGallery(worldState = WorldState(chronosSunAltitude = -1.0f))
}

@Preview(showBackground = true, name = "Gallery - Thermal Stress")
@Composable
fun PreviewGalleryStress() {
    AtmosphereControlsGallery(worldState = WorldState(energyThermalStress = 0.9f))
}
