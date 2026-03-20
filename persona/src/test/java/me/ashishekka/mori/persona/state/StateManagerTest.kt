package me.ashishekka.mori.persona.state

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import me.ashishekka.mori.persona.sensor.StateProviderRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StateManagerTest {

    private val mockRegistry = mockk<StateProviderRegistry> {
        every { data } returns emptyFlow()
        every { providers } returns emptyList()
    }
    
    private val testScope = TestScope(UnconfinedTestDispatcher())
    private lateinit var stateManager: MoriStateManager

    @Before
    fun setUp() {
        stateManager = MoriStateManager(mockRegistry, testScope)
    }

    @After
    fun tearDown() {
        testScope.cancel()
    }

    @Test
    fun `initialState should match default WorldState`() = runTest {
        assertEquals(WorldState(), stateManager.state.value)
    }

    @Test
    fun `update should atomically transform state`() = runTest {
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
        val newState = WorldState(energyBatteryLevel = 0.5f)

        stateManager.update { newState }

        assertEquals(0.5f, stateManager.state.first().energyBatteryLevel)
    }
}
