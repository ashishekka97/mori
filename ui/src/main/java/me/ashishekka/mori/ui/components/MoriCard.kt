package me.ashishekka.mori.ui.components

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
 * Provides a noise-based grain to simulate glass texture.
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
 * A glassmorphic container that blurs the shared [LocalHazeSource] wallpaper layer.
 * 
 * @param modifier The modifier to be applied to the card.
 * @param thermalStress The current thermal stress level. Blurs are disabled when stress > 0.8.
 * @param shape The shape of the card.
 * @param content The content to be displayed inside the card.
 */
@Composable
fun MoriCard(
    modifier: Modifier = Modifier,
    thermalStress: Float = 0f,
    shape: Shape = RoundedCornerShape(24.dp),
    content: @Composable () -> Unit
) {
    val hazeSource = LocalHazeSource.current
    val colors = MoriTheme.colors
    var positionInRoot by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }

    // Tiered Rendering Logic
    val isBlurEnabled = thermalStress < 0.8f && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    
    val blurModifier = if (isBlurEnabled) {
        Modifier.graphicsLayer {
            val blur = 60f // High-quality depth blur
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

    // 1px "Rim Light" border for edge definition
    val rimLightColor = if (colors.isDark) {
        Color.White.copy(alpha = 0.15f)
    } else {
        Color.White.copy(alpha = 0.4f)
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                positionInRoot = coordinates.positionInRoot()
            }
            .clip(shape)
            .then(blurModifier)
            .drawBehind {
                // MIRROR DRAW: Draw the wallpaper layer into the card's background
                hazeSource.layer?.let { layer ->
                    // Offset the draw so the wallpaper pixels match their background position
                    drawContext.canvas.save()
                    drawContext.canvas.translate(-positionInRoot.x, -positionInRoot.y)
                    drawLayer(layer)
                    drawContext.canvas.restore()
                }
                
                // TINT: Apply the frosted surface color over the mirrored wallpaper
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
            .padding(16.dp)
    ) {
        content()
    }
}
