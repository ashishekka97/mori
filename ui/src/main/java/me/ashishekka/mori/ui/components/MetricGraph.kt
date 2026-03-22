package me.ashishekka.mori.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.ashishekka.mori.ui.theme.MoriTheme

/**
 * A high-performance graph component that visualizes a list of data points.
 * 
 * @param data Points normalized between 0.0 and 1.0.
 * @param modifier The modifier to be applied to the canvas.
 * @param lineColor The color of the graph line. Defaults to the theme accent.
 * @param showFill Whether to draw a gradient fill under the line.
 */
@Composable
fun MetricGraph(
    data: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = MoriTheme.colors.accent,
    showFill: Boolean = true
) {
    // Reusable path objects to avoid allocations during draw passes
    val path = remember { Path() }
    val fillPath = remember { Path() }

    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas

        val width = size.width
        val height = size.height
        val spacing = width / (data.size - 1)

        path.reset()
        fillPath.reset()

        // Starting point
        val startX = 0f
        val startY = height - (data[0] * height)
        path.moveTo(startX, startY)
        
        fillPath.moveTo(startX, height)
        fillPath.lineTo(startX, startY)

        // Draw segments with Cubic Bezier smoothing
        for (i in 1 until data.size) {
            val x1 = (i - 1) * spacing
            val y1 = height - (data[i - 1] * height)
            val x2 = i * spacing
            val y2 = height - (data[i] * height)

            // Smooth interpolation between points
            val controlX1 = x1 + (x2 - x1) / 2f
            val controlY1 = y1
            val controlX2 = x1 + (x2 - x1) / 2f
            val controlY2 = y2

            path.cubicTo(controlX1, controlY1, controlX2, controlY2, x2, y2)
            fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, x2, y2)
        }

        // Close the fill path to create a closed shape for the gradient
        fillPath.lineTo(width, height)
        fillPath.close()

        // 1. Draw the Atmospheric Fill
        if (showFill) {
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        lineColor.copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = height
                )
            )
        }

        // 2. Draw the Glowing Line
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}

@Preview(showBackground = true, name = "Metric Graph - Sample")
@Composable
fun PreviewMetricGraph() {
    val sampleData = listOf(0.2f, 0.5f, 0.4f, 0.8f, 0.3f, 0.9f, 0.6f)
    MoriTheme {
        MoriCard(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Column {
                Text("Pulse History", color = MoriTheme.colors.onSurface)
                MetricGraph(
                    data = sampleData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(top = 16.dp)
                )
            }
        }
    }
}
