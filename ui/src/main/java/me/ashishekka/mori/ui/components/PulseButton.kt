package me.ashishekka.mori.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import me.ashishekka.mori.ui.theme.PulseTheme

/**
 * A tactile glass button that correctly and safely applies the theme to its content.
 */
@Composable
fun PulseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    thermalStress: Float = 0f,
    shape: Shape = RoundedCornerShape(12.dp),
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    content: @Composable () -> Unit
) {
    val colors = PulseTheme.colors
    val interactionSource = remember { MutableInteractionSource() }

    PulseGlassBox(
        modifier = modifier
            .clip(shape)
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
        // THEME FIX: Provide the correct onSurface color to the button's content.
        CompositionLocalProvider(
            LocalContentColor provides colors.onSurface
        ) {
            Box(
                modifier = Modifier.padding(contentPadding),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}
