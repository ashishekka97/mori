package me.ashishekka.mori.persona

import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.reflect.full.memberProperties

class WorldStateTest {

    @Test
    fun `verify WorldState contains only primitives and booleans`() {
        val properties = WorldState::class.memberProperties
        
        properties.forEach { property ->
            val typeName = property.returnType.toString()
            val isPrimitive = typeName == "kotlin.Float" || 
                              typeName == "kotlin.Boolean" || 
                              typeName == "kotlin.Int" || 
                              typeName == "kotlin.Long"
            
            assertTrue(
                "Property ${property.name} has type $typeName, which is not a primitive or boolean. " +
                "WorldState must only contain primitives to ensure zero-allocation performance.",
                isPrimitive
            )
        }
    }

    @Test
    fun `verify default values are safe`() {
        val state = WorldState()
        
        // Energy should be full by default
        assertTrue(state.energyBatteryLevel == 1f)
        assertTrue(!state.energyIsCharging)
        
        // Atmos should be clear by default
        assertTrue(state.atmosLightLevel == 1f)
        assertTrue(!state.atmosIsPocketed)
        
        // Vitality sleep should be clear by default
        assertTrue(state.vitalitySleepClarity == 1f)
    }
}
