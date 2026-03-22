package me.ashishekka.mori.ui.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.ashishekka.mori.persona.state.WorldState
import me.ashishekka.mori.ui.components.HazeSource
import me.ashishekka.mori.ui.components.LocalHazeSource
import me.ashishekka.mori.ui.components.MetricGraph
import me.ashishekka.mori.ui.components.MoriButton
import me.ashishekka.mori.ui.components.MoriCard
import me.ashishekka.mori.ui.components.MoriSlider
import me.ashishekka.mori.ui.components.PulseToggle
import me.ashishekka.mori.ui.theme.MoriTheme

/**
 * A dedicated screen to verify and showcase all components of the Mori Pulse Design System.
 */
@Composable
fun ComponentGallery(
    modifier: Modifier = Modifier
) {
    var sunAltitude by remember { mutableFloatStateOf(0.5f) }
    var thermalStress by remember { mutableFloatStateOf(0f) }
    
    val simulatedState = remember(sunAltitude, thermalStress) {
        WorldState(
            chronosSunAltitude = sunAltitude,
            energyThermalStress = thermalStress
        )
    }

    val galleryHazeLayer = rememberGraphicsLayer()

    MoriTheme(simulatedState) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .drawBehind {
                    galleryHazeLayer.record {
                        val skyColor = lerp(
                            Color(0xFF1A237E), 
                            Color(0xFF81D4FA), 
                            ((sunAltitude + 1f) / 2f).coerceIn(0f, 1f)
                        )
                        val accentGlow = lerp(
                            Color(0xFF4A148C), 
                            Color(0xFFFFB74D), 
                            ((sunAltitude + 1f) / 2f).coerceIn(0f, 1f)
                        )
                        drawRect(brush = Brush.verticalGradient(colors = listOf(skyColor, accentGlow, skyColor)))
                        drawCircle(color = accentGlow.copy(alpha = 0.5f), radius = size.minDimension / 2, center = center)
                    }
                    drawLayer(galleryHazeLayer)
                }
        ) {
            CompositionLocalProvider(
                LocalHazeSource provides HazeSource(galleryHazeLayer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Spacer(Modifier.height(64.dp))

                    Text(
                        text = "DESIGN LAB",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = MoriTheme.colors.onSurface
                    )

                    // SECTION: STATE SIMULATOR
                    GallerySection(title = "Simulator") {
                        MoriCard(thermalStress = thermalStress) {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                SimulatorControl(
                                    label = "Sun Altitude",
                                    value = sunAltitude,
                                    onValueChange = { sunAltitude = it },
                                    range = -1f..1f
                                )
                                SimulatorControl(
                                    label = "Thermal Stress",
                                    value = thermalStress,
                                    onValueChange = { thermalStress = it },
                                    range = 0f..1f
                                )
                            }
                        }
                    }

                    // SECTION: PALETTE
                    GallerySection(title = "Living Palette") {
                        MoriCard(thermalStress = thermalStress) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                ColorToken("Accent", MoriTheme.colors.accent)
                                ColorToken("Surface", MoriTheme.colors.surface)
                                ColorToken("Text", MoriTheme.colors.onSurface)
                            }
                        }
                    }

                    // SECTION: INTERACTIVE
                    GallerySection(title = "Interactive") {
                        var clickCount by remember { mutableIntStateOf(0) }
                        var toggleChecked by remember { mutableStateOf(true) }
                        
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            MoriCard(
                                onClick = { clickCount++ },
                                thermalStress = thermalStress,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                    Text("CLICKABLE CARD", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MoriTheme.colors.accent)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Interaction Count: $clickCount", style = MaterialTheme.typography.titleMedium, color = MoriTheme.colors.onSurface)
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                MoriCard(modifier = Modifier.weight(1f), thermalStress = thermalStress) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("PULSE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MoriTheme.colors.onSurface.copy(alpha = 0.6f))
                                        Spacer(Modifier.height(12.dp))
                                        PulseToggle(
                                            checked = toggleChecked,
                                            onCheckedChange = { toggleChecked = it },
                                            thermalStress = thermalStress
                                        )
                                    }
                                }
                                MoriButton(
                                    onClick = {},
                                    modifier = Modifier.weight(1f),
                                    thermalStress = thermalStress
                                ) {
                                    Text("ACTION", color = MoriTheme.colors.onSurface, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // SECTION: VISUALIZERS
                    GallerySection(title = "Visualization") {
                        val dummyTrend = remember { listOf(0.2f, 0.5f, 0.4f, 0.8f, 0.3f, 0.9f, 0.6f) }
                        MoriCard(thermalStress = thermalStress) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("GLOWING METRIC", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MoriTheme.colors.onSurface.copy(alpha = 0.6f))
                                MetricGraph(
                                    data = dummyTrend,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
private fun GallerySection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp,
            color = MoriTheme.colors.accent
        )
        content()
    }
}

@Composable
private fun ColorToken(label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(color))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MoriTheme.colors.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
private fun SimulatorControl(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium, color = MoriTheme.colors.onSurface)
            Text(text = String.format("%.2f", value), style = MaterialTheme.typography.labelMedium, color = MoriTheme.colors.onSurface)
        }
        MoriSlider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewComponentGallery() {
    ComponentGallery()
}
