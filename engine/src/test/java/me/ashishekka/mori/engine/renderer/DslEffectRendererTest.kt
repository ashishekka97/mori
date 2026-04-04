package me.ashishekka.mori.engine.renderer

import io.mockk.mockk
import io.mockk.verify
import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.RuleEvaluator
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import me.ashishekka.mori.engine.core.models.AssetType
import me.ashishekka.mori.engine.core.models.LayerType
import me.ashishekka.mori.engine.core.models.MoriLayer
import me.ashishekka.mori.engine.core.models.RenderProperty
import me.ashishekka.mori.engine.core.util.OpCode
import org.junit.Test

class DslEffectRendererTest {

    private val evaluator = RuleEvaluator()
    private val mockCanvas = mockk<EngineCanvas>(relaxed = true)
    private val state = MoriEngineState()
    private val signals = FloatArray(8)

    @Test
    fun `render should draw rectangle at calculated position`() {
        // Given a layer with static rules for X and Y
        val layer = MoriLayer(id = 1)
        layer.propertyRules[RenderProperty.INDEX_X] = intArrayOf(OpCode.PUSH_CONST, 500f.toBits())
        layer.propertyRules[RenderProperty.INDEX_Y] = intArrayOf(OpCode.PUSH_CONST, 600f.toBits())
        
        val renderer = DslEffectRenderer(layer, evaluator)

        // When
        renderer.update(state, signals)
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
        renderer.update(state, signals)
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
        renderer.update(state, signals)
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

    @Test
    fun `render should draw bitmap when assetType is BITMAP`() {
        val layer = MoriLayer(
            id = 1,
            type = LayerType.RECT,
            resId = 101,
            assetType = AssetType.BITMAP
        )
        val renderer = DslEffectRenderer(layer, evaluator)

        renderer.update(state, signals)
        renderer.render(mockCanvas)

        verify {
            mockCanvas.drawBitmap(eq(101), eq(-50f), eq(-50f), eq(50f), eq(50f), eq(1f))
        }
        // Should NOT draw base rect fill
        verify(exactly = 0) {
            mockCanvas.drawRect(any(), any(), any(), any(), any(), eq(true), any())
        }
    }

    @Test
    fun `render should draw shader when assetType is SHADER`() {
        val layer = MoriLayer(
            id = 1,
            type = LayerType.SHADER,
            resId = 202,
            assetType = AssetType.SHADER
        )
        val renderer = DslEffectRenderer(layer, evaluator)

        renderer.update(state, signals)
        renderer.render(mockCanvas)

        verify {
            mockCanvas.drawShader(eq(202), eq(-50f), eq(-50f), eq(50f), eq(50f), any(), any())
        }
    }

    @Test
    fun `render should call drawPath on canvas when layer type is PATH and resId is provided`() {
        val layer = MoriLayer(
            id = 1,
            type = LayerType.PATH,
            resId = 303
        )
        val renderer = DslEffectRenderer(layer, evaluator)

        renderer.update(state, signals)
        renderer.render(mockCanvas)

        verify {
            mockCanvas.drawPath(eq(303), any(), eq(true), any())
        }
    }
}
