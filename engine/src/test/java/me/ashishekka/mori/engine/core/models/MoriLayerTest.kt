package me.ashishekka.mori.engine.core.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MoriLayerTest {

    @Test
    fun `MoriLayer should initialize with empty rules and pre-allocated buffer`() {
        val layer = MoriLayer(id = 42)
        
        assertEquals(42, layer.id)
        assertEquals(RenderProperty.BUFFER_SIZE, layer.propertyRules.size)
        assertEquals(RenderProperty.BUFFER_SIZE, layer.propertyBuffer.size)
        
        // Ensure all rules are initially null
        for (rule in layer.propertyRules) {
            assertNull(rule)
        }
        
        // Ensure buffer is pre-allocated with zeros
        for (value in layer.propertyBuffer) {
            assertEquals(0f, value, 1e-6f)
        }
    }
}
