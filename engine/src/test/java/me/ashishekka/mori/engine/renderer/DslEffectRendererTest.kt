package me.ashishekka.mori.engine.renderer

import io.mockk.mockk
import io.mockk.verify
import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.RuleEvaluator
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import me.ashishekka.mori.engine.core.models.MoriLayer
import me.ashishekka.mori.engine.core.models.RenderProperty
import me.ashishekka.mori.engine.core.util.OpCode
import org.junit.Test

class DslEffectRendererTest {

    private val evaluator = RuleEvaluator()
    private val mockCanvas = mockk<EngineCanvas>(relaxed = true)
    private val state = MoriEngineState()

    @Test
    fun `render should draw rectangle at calculated position`() {
        // Given a layer with static rules for X and Y
        val layer = MoriLayer(id = 1)
        layer.propertyRules[RenderProperty.INDEX_X] = intArrayOf(OpCode.PUSH_CONST, 500f.toBits())
        layer.propertyRules[RenderProperty.INDEX_Y] = intArrayOf(OpCode.PUSH_CONST, 600f.toBits())
        
        val renderer = DslEffectRenderer(layer, evaluator)

        // When
        renderer.update(state)
        renderer.render(mockCanvas)

        // Then
        // Default size is 100x100
        verify {
            mockCanvas.drawRect(
                450f, // 500 - 50
                550f, // 600 - 50
                550f, // 500 + 50
                650f, // 600 + 50
                any(),
                true
            )
        }
    }

    @Test
    fun `render should apply scale and alpha from buffer`() {
        // Given a layer with Scale=2.0 and Alpha=0.5
        val layer = MoriLayer(id = 1)
        layer.propertyRules[RenderProperty.INDEX_X] = intArrayOf(OpCode.PUSH_CONST, 100f.toBits())
        layer.propertyRules[RenderProperty.INDEX_Y] = intArrayOf(OpCode.PUSH_CONST, 100f.toBits())
        layer.propertyRules[RenderProperty.INDEX_SCALE_X] = intArrayOf(OpCode.PUSH_CONST, 2f.toBits())
        layer.propertyRules[RenderProperty.INDEX_SCALE_Y] = intArrayOf(OpCode.PUSH_CONST, 2f.toBits())
        layer.propertyRules[RenderProperty.INDEX_ALPHA] = intArrayOf(OpCode.PUSH_CONST, 0.5f.toBits())
        
        val renderer = DslEffectRenderer(layer, evaluator)

        // When
        renderer.update(state)
        renderer.render(mockCanvas)

        // Then
        // Scaled size is 200x200 (100 * 2.0)
        // Alpha 0.5 is 127 (approx)
        val expectedColor = (127 shl 24) or (0xFFFFFF)
        verify {
            mockCanvas.drawRect(
                0f,   // 100 - 100
                0f,   // 100 - 100
                200f, // 100 + 100
                200f, // 100 + 100
                expectedColor,
                true
            )
        }
    }
}
