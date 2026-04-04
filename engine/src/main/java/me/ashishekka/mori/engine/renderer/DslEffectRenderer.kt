package me.ashishekka.mori.engine.renderer

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.RuleEvaluator
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import me.ashishekka.mori.engine.core.models.AssetType
import me.ashishekka.mori.engine.core.models.LayerType
import me.ashishekka.mori.engine.core.models.MoriLayer
import me.ashishekka.mori.engine.core.models.RenderProperty

/**
 * A generic, data-driven renderer that executes logic from a [MoriLayer].
 * This is the "Dumb Muscle" that renders whatever the Biome JSON defines.
 * 
 * DESIGN PRINCIPLE:
 * This class has 0 hardcoded math. It purely handovers the [propertyBuffer]
 * results to the [EngineCanvas].
 */
class DslEffectRenderer(
    private val layer: MoriLayer,
    private val evaluator: RuleEvaluator
) : EffectRenderer {

    override val zOrder: Int = layer.zOrder

    private var state: MoriEngineState? = null
    private val trianglePoints = FloatArray(6) // Pre-allocated for zero-allocation drawing

    override fun onSurfaceChanged(width: Int, height: Int, density: Float) {
        // Geometric setup handled by EngineState
    }

    override fun update(state: MoriEngineState, signals: FloatArray) {
        this.state = state
        // 1. Execute all rules for this layer (Zero-Allocation)
        evaluator.evaluateLayer(layer, state, signals)
    }

    override fun getPaletteContribution(): RendererPalette? = null

    override fun render(canvas: EngineCanvas) {
        val buffer = layer.propertyBuffer
        val rules = layer.propertyRules
        val engineState = state ?: return

        // 1. Base Variables
        val scale = engineState.viewportReferenceScale
        val offsetX = engineState.viewportSafeX
        val offsetY = engineState.viewportSafeY

        val x = buffer[RenderProperty.INDEX_X] * scale + offsetX
        val y = buffer[RenderProperty.INDEX_Y] * scale + offsetY
        val rotation = buffer[RenderProperty.INDEX_ROTATION]

        // Use defaults for scale and size if not defined in bytecode
        val scaleX = if (rules[RenderProperty.INDEX_SCALE_X] == null) 1f else buffer[RenderProperty.INDEX_SCALE_X]
        val scaleY = if (rules[RenderProperty.INDEX_SCALE_Y] == null) 1f else buffer[RenderProperty.INDEX_SCALE_Y]
        val width = if (rules[RenderProperty.INDEX_WIDTH] == null) 100f else buffer[RenderProperty.INDEX_WIDTH]
        val height = if (rules[RenderProperty.INDEX_HEIGHT] == null) 100f else buffer[RenderProperty.INDEX_HEIGHT]
        val strokeWidth = if (rules[RenderProperty.INDEX_STROKE_WIDTH] == null) 0f else buffer[RenderProperty.INDEX_STROKE_WIDTH]

        // Visuals - Extract color bits directly
        val alpha = if (rules[RenderProperty.INDEX_ALPHA] == null) 1f else buffer[RenderProperty.INDEX_ALPHA]
        val paintColor = if (rules[RenderProperty.INDEX_COLOR_PRIMARY] == null) {
            0xFFFFFFFF.toInt() 
        } else {
            java.lang.Float.floatToRawIntBits(buffer[RenderProperty.INDEX_COLOR_PRIMARY])
        }
        val strokeColor = if (rules[RenderProperty.INDEX_COLOR_SECONDARY] == null) {
            0xFFFFFFFF.toInt() // Default stroke is White
        } else {
            java.lang.Float.floatToRawIntBits(buffer[RenderProperty.INDEX_COLOR_SECONDARY])
        }

        // 2. Build final colors with alpha
        val paintAlpha = (paintColor ushr 24) and 0xFF
        val strokeAlpha = (strokeColor ushr 24) and 0xFF
        val globalAlpha = alpha.coerceIn(0f, 1f)
        
        val finalColor = ((paintAlpha * globalAlpha).toInt() shl 24) or (paintColor and 0x00FFFFFF)
        val finalStrokeColor = ((strokeAlpha * globalAlpha).toInt() shl 24) or (strokeColor and 0x00FFFFFF)

        // 3. Transform and Draw
        canvas.save()

        // Apply Global Position and Rotation
        canvas.translate(x, y)
        canvas.rotate(rotation)

        // Apply Masking before scaling
        if (layer.maskResId != null) {
            canvas.clipPath(layer.maskResId, 0f, 0f)
        }

        // Apply local scale (combining DSL scale and device scale)
        canvas.scale(scaleX * scale, scaleY * scale)

        // Now draw everything relative to 0,0
        val halfW = width / 2f
        val halfH = height / 2f
        // Stroke width is drawn in scaled space, so we must un-scale it to keep line thickness consistent
        val finalStrokeWidth = strokeWidth / scaleX

        // ZERO-ALLOCATION: Avoid creating a lambda object by using a simple index loop
        val passes = if (finalStrokeWidth > 0f) 2 else 1
        for (pass in 0 until passes) {
            val isStroke = pass == 1
            val colorToUse = if (isStroke) finalStrokeColor else finalColor

            // 1. If we have an Asset (Bitmap/Shader) and it's the Fill pass, draw the asset.
            if (!isStroke && layer.resId != null) {
                when (layer.assetType) {
                    AssetType.BITMAP -> {
                        canvas.drawBitmap(layer.resId, -halfW, -halfH, halfW, halfH, alpha)
                        // If it's a dedicated SHADER or PATH type, we don't draw base geometry.
                        // For RECT/CIRCLE/TRIANGLE, we draw the asset and then optionally the stroke.
                        if (layer.type == LayerType.RECT || layer.type == LayerType.CIRCLE || layer.type == LayerType.TRIANGLE) {
                            // Proceed to stroke pass if needed, but skip base fill.
                            continue 
                        }
                    }
                    AssetType.SHADER -> {
                        canvas.drawShader(layer.resId, -halfW, -halfH, halfW, halfH, buffer, engineState.shaderComplexity)
                        if (layer.type == LayerType.RECT || layer.type == LayerType.CIRCLE || layer.type == LayerType.TRIANGLE || layer.type == LayerType.SHADER) {
                            continue
                        }
                    }
                    AssetType.PATH -> {
                        // PATH assets are drawn via LayerType.PATH block below
                    }
                    AssetType.UNKNOWN -> { /* Fallback to base geometry */ }
                }
            }

            // 2. Draw Base Geometry
            when (layer.type) {
                LayerType.RECT -> {
                    canvas.drawRect(-halfW, -halfH, halfW, halfH, colorToUse, !isStroke, finalStrokeWidth)
                }
                LayerType.CIRCLE -> {
                    canvas.drawCircle(0f, 0f, halfW, colorToUse, !isStroke, finalStrokeWidth)
                }
                LayerType.TRIANGLE -> {
                    trianglePoints[0] = 0f
                    trianglePoints[1] = -halfH
                    trianglePoints[2] = -halfW
                    trianglePoints[3] = halfH
                    trianglePoints[4] = halfW
                    trianglePoints[5] = halfH
                    canvas.drawPolygon(trianglePoints, 6, colorToUse, !isStroke, finalStrokeWidth)
                }
                LayerType.SHADER -> {
                    // Handled above in asset block, if no asset found, draw nothing or fallback RECT
                    if (layer.assetType == AssetType.UNKNOWN) {
                         canvas.drawRect(-halfW, -halfH, halfW, halfH, colorToUse, !isStroke, finalStrokeWidth)
                    }
                }
                LayerType.PATH -> {
                    if (layer.resId != null) {
                        canvas.drawPath(layer.resId, colorToUse, !isStroke, finalStrokeWidth)
                    }
                }
                LayerType.UNKNOWN -> {
                    // Do nothing
                }
            }
        }

        canvas.restore()
        }
        }
