package me.ashishekka.mori.engine.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MoriEngineStateTest {

    @Test
    fun `MoriEngineState should only contain primitive or simple types to ensure zero-allocation`() {
        val clazz = MoriEngineState::class.java
        val fields = clazz.declaredFields

        val allowedTypes = setOf(
            Float::class.javaPrimitiveType,
            Int::class.javaPrimitiveType,
            Long::class.javaPrimitiveType,
            Boolean::class.javaPrimitiveType,
            FloatArray::class.java, // Fact Buffer
            // Also allow the object versions if Kotlin uses them for nullable/generic reasons,
            // but in MoriEngineState they should be primitives.
            java.lang.Float::class.java,
            java.lang.Integer::class.java,
            java.lang.Long::class.java,
            java.lang.Boolean::class.java
        )

        fields.forEach { field ->
            // Skip synthetic fields like $jacocoData
            if (field.isSynthetic) return@forEach

            assertTrue(
                "Field '${field.name}' in MoriEngineState has type ${field.type.name}, which is not an allowed primitive.",
                allowedTypes.contains(field.type)
            )
        }
    }

    @Test
    fun `MoriEngineState should be a regular class with mutable fields, not a data class`() {
        val clazz = MoriEngineState::class.java
        val isDataClass = clazz.declaredMethods.any { it.name.startsWith("component1") }
        assertTrue("MoriEngineState should not be a data class to avoid .copy() allocations.", !isDataClass)
    }

    @Test
    fun `factBuffer should correctly store and retrieve values via indices`() {
        val state = MoriEngineState()
        val index = MoriEngineStateIndices.FACT_BATTERY_LEVEL
        val testValue = 0.75f
        
        state.setFieldValue(index, testValue)
        
        assertEquals(testValue, state.factBuffer[index], 1e-6f)
        assertEquals(testValue, state.getFieldValue(index), 1e-6f)
    }

    @Test
    fun `timeSeconds should be a delegate to FACT_TIME_SECONDS`() {
        val state = MoriEngineState()
        val testValue = 123.456f
        
        state.timeSeconds = testValue
        
        assertEquals(testValue, state.factBuffer[MoriEngineStateIndices.FACT_TIME_SECONDS], 1e-6f)
        assertEquals(testValue, state.timeSeconds, 1e-6f)
    }
}
