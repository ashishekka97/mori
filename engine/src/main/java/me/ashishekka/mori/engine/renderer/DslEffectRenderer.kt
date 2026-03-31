package me.ashishekka.mori.engine.renderer

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.RuleEvaluator
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
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

    override val zOrder: Int = 0 // Will be driven by Biome metadata in Phase 7

    private val signals = FloatArray(8) // Inter-layer communication buffer

    override fun onSurfaceChanged(width: Int, height: Int, density: Float) {
        // Geometric setup handled by EngineState
    }

    override fun update(state: MoriEngineState) {
        // 1. Execute all rules for this layer (Zero-Allocation)
        evaluator.evaluateLayer(layer, state, signals)
    }

    override fun getPaletteContribution(): RendererPalette? = null

    override fun render(canvas: EngineCanvas) {
        val buffer = layer.propertyBuffer
        val rules = layer.propertyRules
        
        // 2. Extract visual properties from the agnostic buffer
        val x = buffer[RenderProperty.INDEX_X]
        val y = buffer[RenderProperty.INDEX_Y]
        
        // Use defaults if no rule is defined for these properties
        val scaleX = if (rules[RenderProperty.INDEX_SCALE_X] == null) 1f else buffer[RenderProperty.INDEX_SCALE_X]
        val scaleY = if (rules[RenderProperty.INDEX_SCALE_Y] == null) 1f else buffer[RenderProperty.INDEX_SCALE_Y]
        val alpha = if (rules[RenderProperty.INDEX_ALPHA] == null) 1f else buffer[RenderProperty.INDEX_ALPHA]
        
        // 3. Render a simple "Prism" (Rectangle) for the Demo
        val paintColor = 0xFFFFFFFF.toInt()
        // Ensure visibility even if light level is low during demo
        val finalAlpha = (alpha.coerceIn(0f, 1f) * 0.5f + 0.5f) 
        val alphaInt = (finalAlpha * 255).toInt()
        val finalColor = (alphaInt shl 24) or (paintColor and 0x00FFFFFF)
        
        val width = 100f * scaleX
        val height = 100f * scaleY
        
        canvas.drawRect(
            x - (width / 2f),
            y - (height / 2f),
            x + (width / 2f),
            y + (height / 2f),
            finalColor,
            true
        )
    }
}
