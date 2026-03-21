package me.ashishekka.mori.ui.theme

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

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun PulseTypographyPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Good Morning", style = PulseTypography.displayLarge, color = Color.White)
        Text(text = "The Island", style = PulseTypography.headlineMedium, color = Color.White)
        Text(text = "Atmosphere", style = PulseTypography.titleLarge, color = Color.LightGray)
        Text(text = "Your vitality is blooming today.", style = PulseTypography.bodyLarge, color = Color.Gray)
        Text(text = "DND ACTIVE", style = PulseTypography.labelSmall, color = Color.Cyan)
    }
}
