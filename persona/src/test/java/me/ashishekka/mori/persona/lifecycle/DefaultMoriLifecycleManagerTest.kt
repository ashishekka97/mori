package me.ashishekka.mori.persona.lifecycle

import io.mockk.mockk
import io.mockk.verify
import me.ashishekka.mori.persona.sensor.StateProviderRegistry
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DefaultMoriLifecycleManagerTest {

    private lateinit var registry: StateProviderRegistry
    private lateinit var lifecycleManager: DefaultMoriLifecycleManager

    @Before
    fun setUp() {
        registry = mockk(relaxed = true)
        lifecycleManager = DefaultMoriLifecycleManager(registry)
    }

    @Test
    fun `first onStart should start registry`() {
        lifecycleManager.onStart()
        
        verify(exactly = 1) { registry.start() }
        assertEquals(MoriLifecycleState.Ready, lifecycleManager.lifecycleState.value)
    }

    @Test
    fun `second onStart should NOT start registry again`() {
        lifecycleManager.onStart()
        lifecycleManager.onStart()
        
        verify(exactly = 1) { registry.start() }
        assertEquals(MoriLifecycleState.Ready, lifecycleManager.lifecycleState.value)
    }

    @Test
    fun `onStop should NOT stop registry if other clients are active`() {
        lifecycleManager.onStart() // Count 1
        lifecycleManager.onStart() // Count 2
        
        lifecycleManager.onStop() // Count 1
        
        verify(exactly = 0) { registry.stop() }
        assertEquals(MoriLifecycleState.Ready, lifecycleManager.lifecycleState.value)
    }

    @Test
    fun `last onStop should stop registry`() {
        lifecycleManager.onStart() // Count 1
        lifecycleManager.onStop()  // Count 0
        
        verify(exactly = 1) { registry.stop() }
        assertEquals(MoriLifecycleState.Stopped, lifecycleManager.lifecycleState.value)
    }

    @Test
    fun `onStop should handle negative counts gracefully`() {
        lifecycleManager.onStop()
        verify(exactly = 0) { registry.stop() }
        assertEquals(MoriLifecycleState.Stopped, lifecycleManager.lifecycleState.value)
    }
}
