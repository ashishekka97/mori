package me.ashishekka.mori.bridge.sync

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import me.ashishekka.mori.bridge.metrics.MetricCalculator
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
    private lateinit var metricCalculator: MetricCalculator
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
        metricCalculator = MetricCalculator()
        testScope = TestScope(UnconfinedTestDispatcher())
    }

    @Test
    fun `start should collect state and sync fields to engine mirror`() = testScope.runTest {
        // Given
        val synchronizer = StateSynchronizer(mockStateManager, mockEngine, metricCalculator, testScope)
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
    fun `updateViewport should calculate and sync stage metrics`() {
        // Given a 1080x2400 screen
        metricCalculator.updateMetrics(1080, 2400, 1.0f)
        val synchronizer = StateSynchronizer(mockStateManager, mockEngine, metricCalculator, testScope)

        // When (1000x1000 reference)
        synchronizer.updateViewport(1000f, 1000f)

        // Then (2400/1000 = 2.4 scale for FILL mode)
        assertEquals(2.4f, engineState.viewportReferenceScale, 0.01f)
        assertEquals(2400f, engineState.viewportSafeWidth, 0.01f)
        // Offset X should be (1080 - 2400) / 2 = -660
        assertEquals(-660f, engineState.viewportSafeX, 0.01f)
    }

    @Test
    fun `stop should cancel sync job`() = testScope.runTest {
        // Given
        val synchronizer = StateSynchronizer(mockStateManager, mockEngine, metricCalculator, testScope)
        
        // When
        synchronizer.start()
        synchronizer.stop()
        stateFlow.value = WorldState(chronosTimeProgress = 0.9f)

        // Then (Mirror should not have updated)
        assertEquals(0f, engineState.chronosTimeProgress)
    }
}
