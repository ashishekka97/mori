package me.ashishekka.mori.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.ashishekka.mori.persona.state.WorldState

// We use the system's Sans-Serif family for maximum performance and zero-allocation loading.
// The Pulse Design System relies on weight and spacing to create its atmosphere.
val PulseFontFamily = FontFamily.SansSerif

val PulseTypography = Typography(
    // Used for atmospheric greetings and large time displays
    displayLarge = TextStyle(
        fontFamily = PulseFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    
    // Used for section headers in the dashboard
    headlineMedium = TextStyle(
        fontFamily = PulseFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    
    // Used for card titles and prominent data points
    titleLarge = TextStyle(
        fontFamily = PulseFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    
    // Primary reading text
    bodyLarge = TextStyle(
        fontFamily = PulseFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    
    // Small metadata and status chip labels
    labelSmall = TextStyle(
        fontFamily = PulseFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

@Preview(name = "Day Mode", showBackground = true)
@Composable
fun AtmosphereDayPreview() {
    val state = WorldState(chronosSunAltitude = 1.0f) // Noon
    val colors = rememberAtmosphereColors(state)
    AtmospherePreview(colors, "Good Morning")
}

@Preview(name = "Night Mode", showBackground = true)
@Composable
fun AtmosphereNightPreview() {
    val state = WorldState(chronosSunAltitude = -1.0f) // Midnight
    val colors = rememberAtmosphereColors(state)
    AtmospherePreview(colors, "Good Night")
}

@Composable
private fun AtmospherePreview(colors: AtmosphereColors, greeting: String) {
    Box(modifier = Modifier.background(Color.DarkGray).padding(16.dp)) {
        Column(
            modifier = Modifier
                .background(colors.surface)
                .padding(24.dp)
        ) {
            Text(text = greeting, style = PulseTypography.displayLarge, color = colors.onSurface)
            Text(text = "The Island", style = PulseTypography.headlineMedium, color = colors.accent)
            Text(
                text = "Your vitality is blooming today.", 
                style = PulseTypography.bodyLarge, 
                color = colors.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
