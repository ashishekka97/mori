package me.ashishekka.mori.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import me.ashishekka.mori.ui.theme.MoriTheme

/**
 * A tactile glass button that uses Mori's glassmorphic effect.
 */
@Composable
fun MoriButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    thermalStress: Float = 0f,
    shape: Shape = RoundedCornerShape(12.dp),
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = MoriTheme.colors
    val interactionSource = remember { MutableInteractionSource() }

    MoriGlassBox(
        modifier = modifier
            .clip(shape) // RIPPLE FIX: Clip before clickable
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = colors.accent),
                enabled = enabled,
                onClick = onClick
            ),
        thermalStress = thermalStress,
        shape = shape,
        borderAlpha = 0.6f
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMoriButton() {
    Column(modifier = Modifier.padding(16.dp)) {
        val goldenHour = WorldState(chronosSunAltitude = 0.5f)
        MoriTheme(goldenHour) {
            MoriButton(onClick = {}) {
                Text("Golden Button", color = MoriTheme.colors.onSurface)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val midnight = WorldState(chronosSunAltitude = -1.0f)
        MoriTheme(midnight) {
            MoriButton(onClick = {}) {
                Text("Midnight Button", color = MoriTheme.colors.onSurface)
            }
        }
    }
}
