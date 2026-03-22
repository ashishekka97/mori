package me.ashishekka.mori.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.ashishekka.mori.ui.theme.MoriTheme

/**
 * A slider with a glassmorphic track and a themed thumb.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoriSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    thermalStress: Float = 0f,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f
) {
    val colors = MoriTheme.colors

    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        valueRange = valueRange,
        colors = SliderDefaults.colors(
            thumbColor = colors.accent,
            activeTrackColor = colors.accent,
            inactiveTrackColor = Color.Transparent
        ),
        track = { sliderState ->
            val trackBaseColor = if (colors.isDark) {
                Color.White.copy(alpha = 0.1f)
            } else {
                Color.Black.copy(alpha = 0.05f)
            }

            // PIXEL PERFECT: Apply the glass effect directly to the M3 Track modifier.
            // This ensures the Slider centers our glass perfectly with the thumb and ticks.
            SliderDefaults.Track(
                sliderState = sliderState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp) // The glass track height
                    .moriGlassBackground(thermalStress, shape = CircleShape, borderAlpha = 0.2f),
                colors = SliderDefaults.colors(
                    activeTrackColor = colors.accent,
                    inactiveTrackColor = trackBaseColor
                ),
                // Internal M3 track drawing will happen on top of our glass modifier
                drawStopIndicator = null // Optional: hide default dots if they clutter the glass
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewMoriSlider() {
    MoriTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            MoriSlider(value = 0.5f, onValueChange = {})
        }
    }
}
