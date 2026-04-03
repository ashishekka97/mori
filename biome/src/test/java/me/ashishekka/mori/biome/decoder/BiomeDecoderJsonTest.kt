package me.ashishekka.mori.biome.decoder

import me.ashishekka.mori.biome.models.BiomeModel
import me.ashishekka.mori.engine.core.models.AssetType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BiomeDecoderJsonTest {
    @Test
    fun testDecoderParsesResId() {
        val json = """
        {
          "id": "childhood_canvas",
          "name": "Test",
          "resources": [
            { "id": 1, "path": "river_curve.path", "type": "PATH" }
          ],
          "layers": [
            {
              "id": 8,
              "type": "PATH",
              "resId": 1,
              "zOrder": -4,
              "expressions": {}
            }
          ]
        }
        """.trimIndent()

        val model = BiomeDecoder.decode(json)
        assertNotNull("Model should not be null", model)
        
        val layers = BiomeDecoder.compileToLayers(model)
        val layer = layers.find { it.id == 8 }
        assertNotNull("Layer should be compiled", layer)
        assertEquals("resId should be 1", 1, layer?.resId)
        assertEquals("assetType should be PATH", AssetType.PATH, layer?.assetType)
    }
}
