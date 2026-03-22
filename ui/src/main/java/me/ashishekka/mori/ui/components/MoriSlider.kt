package me.ashishekka.mori.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            activeTrackColor = Color.Transparent, // We draw our own
            inactiveTrackColor = Color.Transparent
        ),
        track = { sliderState ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                // 1. FULL GLASS TRACK (Background)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .moriGlassBackground(thermalStress, shape = CircleShape, borderAlpha = 0.2f)
                )

                // 2. ACTIVE FILAMENT (The glowing core)
                // We calculate the width based on the current slider progress
                val fraction = (value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction.coerceIn(0f, 1f))
                        .height(4.dp)
                        .padding(horizontal = 2.dp)
                        .clip(CircleShape)
                        .background(colors.accent)
                )
            }
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
