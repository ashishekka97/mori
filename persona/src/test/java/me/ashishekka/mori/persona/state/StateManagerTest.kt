package me.ashishekka.mori.persona.state

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import me.ashishekka.mori.persona.sensor.StateProviderRegistry
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StateManagerTest {

    private val mockRegistry = mockk<StateProviderRegistry> {
        every { data } returns emptyFlow()
        every { providers } returns emptyList()
    }

    @Test
    fun `initialState should match default WorldState`() = runTest {
        val stateManager = MoriStateManager(mockRegistry, backgroundScope)
        assertEquals(WorldState(), stateManager.state.value)
    }

    @Test
    fun `update should atomically transform state`() = runTest {
        val stateManager = MoriStateManager(mockRegistry, backgroundScope)
        val numCoroutines = 100
        val incrementPerCoroutine = 0.01f

        // Launch concurrent updates
        val jobs = List(numCoroutines) {
            launch(Dispatchers.Default) {
                stateManager.update { current ->
                    current.copy(vitalityStepsProgress = current.vitalityStepsProgress + incrementPerCoroutine)
                }
            }
        }
        jobs.forEach { it.join() }

        // Expected total is 1.0f (100 * 0.01)
        assertEquals(1.0f, stateManager.state.value.vitalityStepsProgress, 0.0001f)
    }

    @Test
    fun `stateFlow should emit updated value`() = runTest {
        val stateManager = MoriStateManager(mockRegistry, backgroundScope)
        val newState = WorldState(energyBatteryLevel = 0.5f)

        stateManager.update { newState }

        assertEquals(0.5f, stateManager.state.first().energyBatteryLevel)
    }
}
