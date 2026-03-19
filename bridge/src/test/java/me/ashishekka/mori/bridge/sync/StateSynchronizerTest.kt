package me.ashishekka.mori.bridge.sync

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import me.ashishekka.mori.engine.core.MoriEngine
import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.persona.state.StateManager
import me.ashishekka.mori.persona.state.WorldState
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StateSynchronizerTest {

    private lateinit var mockStateManager: StateManager
    private lateinit var mockEngine: MoriEngine
    private lateinit var engineState: MoriEngineState
    private lateinit var testScope: TestScope
    
    private val stateFlow = MutableStateFlow(WorldState())

    @Before
    fun setUp() {
        mockStateManager = mockk {
            every { state } returns stateFlow
        }
        engineState = MoriEngineState()
        mockEngine = mockk(relaxed = true) {
            every { state } returns engineState
        }
        testScope = TestScope(UnconfinedTestDispatcher())
    }

    @Test
    fun `start should collect state and sync fields to engine mirror`() = testScope.runTest {
        // Given
        val synchronizer = StateSynchronizer(mockStateManager, mockEngine, testScope)
        val newState = WorldState(
            chronosTimeProgress = 0.5f,
            energyBatteryLevel = 0.8f,
            zenIsDndActive = true
        )

        // When
        synchronizer.start()
        stateFlow.value = newState

        // Then
        assertEquals(0.5f, engineState.chronosTimeProgress)
        assertEquals(0.8f, engineState.energyBatteryLevel)
        assertEquals(true, engineState.zenIsDndActive)
        
        verify { mockEngine.requestFrame() }
        
        synchronizer.stop()
    }

    @Test
    fun `stop should cancel sync job`() = testScope.runTest {
        // Given
        val synchronizer = StateSynchronizer(mockStateManager, mockEngine, testScope)
        
        // When
        synchronizer.start()
        synchronizer.stop()
        stateFlow.value = WorldState(chronosTimeProgress = 0.9f)

        // Then (Mirror should not have updated)
        assertEquals(0f, engineState.chronosTimeProgress)
    }
}
