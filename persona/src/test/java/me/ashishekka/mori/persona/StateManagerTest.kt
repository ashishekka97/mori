package me.ashishekka.mori.persona

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StateManagerTest {

    @Test
    fun `initialState should match default WorldState`() = runTest {
        val stateManager = MoriStateManager()
        assertEquals(WorldState(), stateManager.state.value)
    }

    @Test
    fun `update should atomically transform state`() = runTest {
        val stateManager = MoriStateManager()
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
        val stateManager = MoriStateManager()
        val newState = WorldState(energyBatteryLevel = 0.5f)

        stateManager.update { newState }

        assertEquals(0.5f, stateManager.state.first().energyBatteryLevel)
    }
}
