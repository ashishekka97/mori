package me.ashishekka.mori.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.ashishekka.mori.persona.state.WorldState
import me.ashishekka.mori.ui.theme.PulseTheme

/**
 * A glassmorphic container that ensures content remains sharp while the background is blurred.
 */
@Composable
fun PulseCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    thermalStress: Float = 0f,
    shape: Shape = RoundedCornerShape(24.dp),
    content: @Composable () -> Unit
) {
    val colors = PulseTheme.colors
    val interactionSource = remember { MutableInteractionSource() }

    val interactionModifier = if (onClick != null) {
        Modifier
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = colors.accent),
                onClick = onClick
            )
    } else {
        Modifier.clip(shape)
    }

    PulseGlassBox(
        modifier = modifier.then(interactionModifier),
        thermalStress = thermalStress,
        shape = shape
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}

@Preview(showBackground = true, name = "Pulse Card - Atmospheric States")
@Composable
fun PreviewPulseCard() {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val goldenHour = WorldState(chronosSunAltitude = 0.5f)
        PulseTheme(goldenHour) {
            PulseCard(
                modifier = Modifier.size(width = 300.dp, height = 120.dp),
                onClick = {},
                thermalStress = 0f
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Pulse Clickable Card", color = PulseTheme.colors.onSurface)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val midnight = WorldState(chronosSunAltitude = -1.0f)
        PulseTheme(midnight) {
            PulseCard(
                modifier = Modifier.size(width = 300.dp, height = 120.dp),
                thermalStress = 0f
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Pulse Midnight Glass", color = PulseTheme.colors.onSurface)
                }
            }
        }
    }
}
