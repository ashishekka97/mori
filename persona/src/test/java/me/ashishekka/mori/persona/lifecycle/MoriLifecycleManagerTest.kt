package me.ashishekka.mori.persona.lifecycle

import me.ashishekka.mori.persona.sensor.StateProviderRegistry
import me.ashishekka.mori.persona.sensor.StateProvider
import me.ashishekka.mori.persona.sensor.EnergyRating
import me.ashishekka.mori.persona.sensor.StateUpdate

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MoriLifecycleManagerTest {

    private class MockStateProviderRegistry : StateProviderRegistry {
        override val energyRating = EnergyRating.GRADE_A
        override val providers: List<StateProvider> = emptyList()
        override val data = kotlinx.coroutines.flow.emptyFlow<StateUpdate>()
        var startCalled = false
        var stopCalled = false

        override fun start() {
            startCalled = true
        }

        override fun stop() {
            stopCalled = true
        }
    }

    @Test
    fun `DefaultMoriLifecycleManager should start Stopped`() = runTest {
        val registry = MockStateProviderRegistry()
        val manager = DefaultMoriLifecycleManager(registry)

        assertEquals(MoriLifecycleState.Stopped, manager.lifecycleState.value)
    }

    @Test
    fun `onStart should call registry start and transition to Ready`() = runTest {
        val registry = MockStateProviderRegistry()
        val manager = DefaultMoriLifecycleManager(registry)

        manager.onStart()

        assertTrue("Registry.start() should be called", registry.startCalled)
        assertEquals(MoriLifecycleState.Ready, manager.lifecycleState.value)
    }

    @Test
    fun `onStop should call registry stop and transition to Stopped`() = runTest {
        val registry = MockStateProviderRegistry()
        val manager = DefaultMoriLifecycleManager(registry)

        manager.onStart() // First move to Ready
        manager.onStop()

        assertTrue("Registry.stop() should be called", registry.stopCalled)
        assertEquals(MoriLifecycleState.Stopped, manager.lifecycleState.value)
    }
}
