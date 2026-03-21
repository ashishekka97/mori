package me.ashishekka.mori.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
            activeTrackColor = colors.accent.copy(alpha = 0.5f),
            inactiveTrackColor = colors.surface.copy(alpha = 0.2f)
        ),
        track = { sliderState ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .moriGlass(thermalStress, shape = CircleShape, borderAlpha = 0.2f)
            )
            // Note: M3 Slider handles the active portion internally, 
            // we provide the base glass track here.
            SliderDefaults.Track(
                sliderState = sliderState,
                colors = SliderDefaults.colors(
                    activeTrackColor = colors.accent,
                    inactiveTrackColor = androidx.compose.ui.graphics.Color.Transparent
                )
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
