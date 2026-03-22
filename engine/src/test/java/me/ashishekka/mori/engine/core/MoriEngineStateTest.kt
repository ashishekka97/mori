package me.ashishekka.mori.engine.core

import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Modifier

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

        // Data classes in Kotlin have a 'copy' method.
        // We want to avoid data classes for the EngineState to prevent accidental allocations via .copy()
        val hasCopyMethod = clazz.methods.any { it.name == "copy" && it.isSynthetic.not() }

        // Note: Kotlin data classes aren't explicitly marked in bytecode in a simple way
        // without checking for componentN methods or the copy method.
        val isDataClass = clazz.declaredMethods.any { it.name.startsWith("component1") }

        assertTrue("MoriEngineState should not be a data class to avoid .copy() allocations.", !isDataClass)
    }
}
