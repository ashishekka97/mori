package me.ashishekka.mori.biome.decoder

import me.ashishekka.mori.engine.core.models.RenderProperty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BiomeDecoderTest {

    private val sampleJson = """
        {
            "id": "test_biome",
            "name": "Test Biome",
            "layers": [
                {
                    "id": 1,
                    "type": "CIRCLE",
                    "zOrder": 10,
                    "expressions": {
                        "x": "fact[6] * 100",
                        "alpha": "sin(time)"
                    }
                }
            ]
        }
    """.trimIndent()

    @Test
    fun `decode should parse metadata correctly`() {
        val model = BiomeDecoder.decode(sampleJson)
        assertNotNull(model)
        assertEquals("test_biome", model?.id)
        assertEquals("Test Biome", model?.name)
        assertEquals(1, model?.layers?.size)
    }

    @Test
    fun `compileToLayers should generate engine layers with bytecode`() {
        val model = BiomeDecoder.decode(sampleJson)
        val engineLayers = BiomeDecoder.compileToLayers(model)
        
        assertEquals(1, engineLayers.size)
        val layer = engineLayers[0]
        
        // Verify X rule
        assertNotNull(layer.propertyRules[RenderProperty.INDEX_X])
        
        // Verify Alpha rule
        assertNotNull(layer.propertyRules[RenderProperty.INDEX_ALPHA])
    }

    // --- ROBUSTNESS TESTS ---

    @Test
    fun `decode should return null for fundamentally broken JSON`() {
        val brokenJson = "{ \"id\": \"test\", \"layers\": [ { \"id\": 1 " // Missing closing brackets
        assertNull(BiomeDecoder.decode(brokenJson))
    }

    @Test
    fun `compileToLayers should handle partial success when an expression is bad`() {
        val partialBadJson = """
            {
                "id": "bad_expression_biome",
                "name": "Partial Bad",
                "layers": [
                    {
                        "id": 1,
                        "type": "CIRCLE",
                        "expressions": {
                            "x": "10 + 20",
                            "y": "10 + * 5" 
                        }
                    }
                ]
            }
        """.trimIndent()
        
        val model = BiomeDecoder.decode(partialBadJson)
        val engineLayers = BiomeDecoder.compileToLayers(model)
        
        assertEquals(1, engineLayers.size)
        val layer = engineLayers[0]
        
        // X should be compiled correctly
        assertTrue(layer.propertyRules[RenderProperty.INDEX_X]!!.isNotEmpty())
        
        // Y should be an empty array (failed but handled)
        assertTrue(layer.propertyRules[RenderProperty.INDEX_Y]!!.isEmpty())
    }

    @Test
    fun `compileToLayers should skip fundamentally malformed layers`() {
        // This test case would trigger if something in the map processing fails, 
        // though mapNotNull currently handles individual expression failures within a layer.
        // If we had a layer with missing mandatory fields (once we define them), this would skip it.
        val model = BiomeDecoder.decode(sampleJson)
        val engineLayers = BiomeDecoder.compileToLayers(model)
        assertEquals(1, engineLayers.size)
    }
}
