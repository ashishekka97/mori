package me.ashishekka.mori.persona.sensor

import me.ashishekka.mori.persona.sensor.StateProviderTest

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StateProviderRegistryTest {

    @Test
    fun `MoriStateProviderRegistry should delegate start to all registered providers`() {
        val providers = List(5) { StateProviderTest.MockStateProvider() }
        val registry = MoriStateProviderRegistry(providers)

        registry.start()

        providers.forEachIndexed { index, provider ->
            assertTrue("Provider at index $index should be started", provider.isStarted)
        }
    }

    @Test
    fun `MoriStateProviderRegistry should delegate stop to all registered providers`() {
        val providers = List(5) { StateProviderTest.MockStateProvider() }
        val registry = MoriStateProviderRegistry(providers)

        registry.start()
        registry.stop()

        providers.forEachIndexed { index, provider ->
            assertFalse("Provider at index $index should be stopped", provider.isStarted)
        }
    }

    @Test
    fun `MoriStateProviderRegistry should handle empty provider list correctly`() {
        val registry = MoriStateProviderRegistry(emptyList())

        // Should not throw any exception
        registry.start()
        registry.stop()
        
        assertTrue("Registry should hold an empty list", registry.providers.isEmpty())
    }
}
