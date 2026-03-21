package me.ashishekka.mori.ui.components

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import me.ashishekka.mori.ui.theme.MoriTheme

/**
 * AGSL Shader for advanced "frosting" effect.
 */
private const val FROST_SHADER = """
    uniform shader composable;
    uniform float noiseAmount;

    float rand(vec2 co) {
        return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);
    }

    vec4 main(vec2 coords) {
        vec4 color = composable.eval(coords);
        float noise = (rand(coords * 0.01) - 0.5) * noiseAmount;
        return vec4(color.rgb + noise, color.a);
    }
"""

/**
 * A reusable modifier that applies Mori's glassmorphic effect.
 * It handles the mirrored backdrop blur, AGSL frosting, and rim light.
 */
fun Modifier.moriGlass(
    thermalStress: Float = 0f,
    shape: Shape,
    borderAlpha: Float = 0.4f
): Modifier = composed {
    val hazeSource = LocalHazeSource.current
    val colors = MoriTheme.colors
    var positionInRoot by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }

    // Tiered Rendering Logic
    val isBlurEnabled = thermalStress < 0.8f && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    
    val blurModifier = if (isBlurEnabled) {
        Modifier.graphicsLayer {
            val blur = 60f
            val baseEffect = RenderEffect.createBlurEffect(
                blur, blur, android.graphics.Shader.TileMode.CLAMP
            )
            
            renderEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val shader = RuntimeShader(FROST_SHADER)
                shader.setFloatUniform("noiseAmount", 0.03f)
                RenderEffect.createChainEffect(
                    RenderEffect.createRuntimeShaderEffect(shader, "composable"),
                    baseEffect
                )
            } else {
                baseEffect
            }.asComposeRenderEffect()
        }
    } else {
        Modifier
    }

    val rimLightColor = if (colors.isDark) {
        Color.White.copy(alpha = 0.15f)
    } else {
        Color.White.copy(alpha = borderAlpha)
    }

    this
        .onGloballyPositioned { coordinates ->
            positionInRoot = coordinates.positionInRoot()
        }
        .clip(shape)
        .then(blurModifier)
        .drawBehind {
            // 1. MIRROR DRAW (Backdrop Blur Source)
            hazeSource.layer?.let { layer ->
                drawContext.canvas.save()
                drawContext.canvas.translate(-positionInRoot.x, -positionInRoot.y)
                drawLayer(layer)
                drawContext.canvas.restore()
            }
            
            // 2. TINT (Frosted Surface)
            drawRect(color = colors.surface)
        }
        .border(
            BorderStroke(
                1.dp,
                Brush.verticalGradient(
                    listOf(rimLightColor, Color.Transparent)
                )
            ),
            shape = shape
        )
}
