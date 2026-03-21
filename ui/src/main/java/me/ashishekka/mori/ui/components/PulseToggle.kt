package me.ashishekka.mori.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.ashishekka.mori.ui.theme.MoriTheme

/**
 * A custom switch with a glassmorphic track and an accent-colored thumb.
 */
@Composable
fun PulseToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    thermalStress: Float = 0f,
    enabled: Boolean = true
) {
    val colors = MoriTheme.colors
    val interactionSource = remember { MutableInteractionSource() }

    // Animations
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 24.dp else 0.dp,
        animationSpec = tween(durationMillis = 200),
        label = "thumbOffset"
    )
    
    val thumbColor by animateColorAsState(
        targetValue = if (checked) colors.accent else Color.Gray.copy(alpha = 0.5f),
        animationSpec = tween(durationMillis = 200),
        label = "thumbColor"
    )

    val thumbScale by animateFloatAsState(
        targetValue = if (checked) 1.1f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "thumbScale"
    )

    Box(
        modifier = modifier
            .width(52.dp)
            .height(28.dp)
            .moriGlassBackground(thermalStress, shape = CircleShape, borderAlpha = 0.3f)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = { onCheckedChange(!checked) }
            )
            .padding(4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // The Thumb (Lives outside the blurred background node)
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(20.dp)
                .scale(thumbScale)
                .clip(CircleShape)
                .background(thumbColor)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPulseToggle() {
    Column(modifier = Modifier.padding(16.dp)) {
        MoriTheme {
            PulseToggle(checked = true, onCheckedChange = {})
            Spacer(modifier = Modifier.height(16.dp))
            PulseToggle(checked = false, onCheckedChange = {})
        }
    }
}
