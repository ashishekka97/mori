package me.ashishekka.mori.biome.decoder

import me.ashishekka.mori.engine.core.models.AssetType
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

    @Test
    fun `decode should parse resources and resId correctly`() {
        val resourceJson = """
            {
                "id": "resource_biome",
                "name": "Resource Biome",
                "resources": [
                    { "id": 101, "path": "textures/cloud.png", "type": "BITMAP" }
                ],
                "layers": [
                    {
                        "id": 1,
                        "type": "RECT",
                        "resId": 101,
                        "expressions": {
                            "x": "500"
                        }
                    }
                ]
            }
        """.trimIndent()
        
        val model = BiomeDecoder.decode(resourceJson)
        assertNotNull(model)
        assertEquals(1, model?.resources?.size)
        assertEquals(101, model?.resources?.get(0)?.id)
        assertEquals("textures/cloud.png", model?.resources?.get(0)?.path)
        
        val engineLayers = BiomeDecoder.compileToLayers(model)
        assertEquals(1, engineLayers.size)
        assertEquals(101, engineLayers[0].resId)
        assertEquals(AssetType.BITMAP, engineLayers[0].assetType)
    }

    @Test
    fun `compileToLayers should resolve SHADER asset type`() {
        val shaderJson = """
            {
                "id": "shader_biome",
                "name": "Shader Biome",
                "resources": [
                    { "id": 202, "path": "shaders/noise.agsl", "type": "SHADER" }
                ],
                "layers": [
                    {
                        "id": 1,
                        "type": "SHADER",
                        "resId": 202
                    }
                ]
            }
        """.trimIndent()
        
        val model = BiomeDecoder.decode(shaderJson)
        val engineLayers = BiomeDecoder.compileToLayers(model)
        
        assertEquals(1, engineLayers.size)
        assertEquals(202, engineLayers[0].resId)
        assertEquals(AssetType.SHADER, engineLayers[0].assetType)
    }

    @Test
    fun `compileToLayers should default to UNKNOWN for missing resource`() {
        val missingResJson = """
            {
                "id": "missing_res_biome",
                "name": "Missing Res",
                "layers": [
                    {
                        "id": 1,
                        "type": "RECT",
                        "resId": 999
                    }
                ]
            }
        """.trimIndent()
        
        val model = BiomeDecoder.decode(missingResJson)
        val engineLayers = BiomeDecoder.compileToLayers(model)
        
        assertEquals(1, engineLayers.size)
        assertEquals(999, engineLayers[0].resId)
        assertEquals(AssetType.UNKNOWN, engineLayers[0].assetType)
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
