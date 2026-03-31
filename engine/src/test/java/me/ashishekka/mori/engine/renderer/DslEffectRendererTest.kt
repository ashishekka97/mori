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
        // Default size is 100x100, drawn around 0,0 due to translate
        verify {
            mockCanvas.translate(eq(500f), eq(600f))
            mockCanvas.drawRect(
                eq(-50f), // 100 / 2
                eq(-50f),
                eq(50f),
                eq(50f),
                any(),
                eq(true),
                eq(0f)
            )
        }
    }

    @Test
    fun `render should draw stroke if stroke_width is defined`() {
        val layer = MoriLayer(id = 1)
        layer.propertyRules[RenderProperty.INDEX_X] = intArrayOf(OpCode.PUSH_CONST, 100f.toBits())
        layer.propertyRules[RenderProperty.INDEX_Y] = intArrayOf(OpCode.PUSH_CONST, 100f.toBits())
        layer.propertyRules[RenderProperty.INDEX_STROKE_WIDTH] = intArrayOf(OpCode.PUSH_CONST, 5f.toBits())
        
        val renderer = DslEffectRenderer(layer, evaluator)

        // When
        renderer.update(state)
        renderer.render(mockCanvas)

        // Then
        // Verify FILL pass
        verify {
            mockCanvas.translate(eq(100f), eq(100f))
            mockCanvas.drawRect(eq(-50f), eq(-50f), eq(50f), eq(50f), any(), eq(true), eq(5f))
        }
        // Verify STROKE pass
        verify {
            mockCanvas.drawRect(eq(-50f), eq(-50f), eq(50f), eq(50f), any(), eq(false), eq(5f))
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
        // Scale is applied via canvas.scale
        val expectedColor = (127 shl 24) or 0xFFFFFF
        verify {
            mockCanvas.translate(eq(100f), eq(100f))
            mockCanvas.scale(eq(2f), eq(2f), eq(0f), eq(0f))
            mockCanvas.drawRect(
                eq(-50f),
                eq(-50f),
                eq(50f),
                eq(50f),
                eq(expectedColor),
                eq(true),
                eq(0f)
            )
        }
    }
}
