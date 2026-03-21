package me.ashishekka.mori.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.ashishekka.mori.persona.state.WorldState
import me.ashishekka.mori.ui.theme.MoriTheme

/**
 * A glassmorphic container that ensures content remains sharp while the background is blurred.
 */
@Composable
fun MoriCard(
    modifier: Modifier = Modifier,
    thermalStress: Float = 0f,
    shape: Shape = RoundedCornerShape(24.dp),
    content: @Composable () -> Unit
) {
    MoriGlassBox(
        modifier = modifier,
        thermalStress = thermalStress,
        shape = shape
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Preview(showBackground = true, name = "Mori Card - Atmospheric States")
@Composable
fun PreviewMoriCard() {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val goldenHour = WorldState(chronosSunAltitude = 0.5f)
        MoriTheme(goldenHour) {
            MoriCard(
                modifier = Modifier.size(width = 300.dp, height = 120.dp),
                thermalStress = 0f
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Golden Hour Glass", color = MoriTheme.colors.onSurface)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val midnight = WorldState(chronosSunAltitude = -1.0f)
        MoriTheme(midnight) {
            MoriCard(
                modifier = Modifier.size(width = 300.dp, height = 120.dp),
                thermalStress = 0f
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Midnight Glass", color = MoriTheme.colors.onSurface)
                }
            }
        }
    }
}
