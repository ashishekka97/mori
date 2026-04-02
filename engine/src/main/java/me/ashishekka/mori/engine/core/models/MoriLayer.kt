package me.ashishekka.mori.engine.core.models

/**
 * A "Dumb Muscle" container for a single rendering layer.
 * Holds the pre-compiled bytecode rules and a pre-allocated buffer for property results.
 * 
 * DESIGN PRINCIPLE:
 * This class is a pure data structure. It contains 0 logic and is designed for
 * high-performance access by the RuleEvaluator and Renderers.
 */
class MoriLayer(
    val id: Int,
    val type: LayerType = LayerType.RECT,
    val zOrder: Int = 0,
    val resId: Int? = null,
    /** 
     * The executable machine code for each property in [RenderProperty].
     * If a rule is null, the corresponding property value remains unchanged or defaults to 0.
     */
    val propertyRules: Array<IntArray?> = arrayOfNulls(RenderProperty.BUFFER_SIZE)
) {
    /** 
     * The flat memory buffer where [RuleEvaluator] writes the results of rule execution.
     * Renderers read from this buffer during the DRAW phase.
     */
    val propertyBuffer = FloatArray(RenderProperty.BUFFER_SIZE)
}
