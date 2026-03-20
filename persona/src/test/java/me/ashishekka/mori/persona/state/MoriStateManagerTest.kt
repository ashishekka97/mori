package me.ashishekka.mori.persona.state

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import me.ashishekka.mori.persona.sensor.StateProvider
import me.ashishekka.mori.persona.sensor.StateProviderRegistry
import me.ashishekka.mori.persona.sensor.StateUpdate
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MoriStateManagerTest {

    private lateinit var mockRegistry: StateProviderRegistry
    private lateinit var mockProvider: StateProvider
    private val providerFlow = MutableSharedFlow<StateUpdate>(replay = 1)

    @Before
    fun setUp() {
        mockProvider = mockk {
            every { data } returns providerFlow
        }
        mockRegistry = mockk {
            every { providers } returns listOf(mockProvider)
            every { data } returns providerFlow
        }
    }

    @Test
    fun `should update state when energy update is received`() = runTest {
        // Given
        val stateManager = MoriStateManager(mockRegistry, backgroundScope)
        val energyUpdate = StateUpdate.Energy(
            batteryLevel = 0.75f,
            isCharging = true,
            isPowerSaveMode = false
        )

        // When
        providerFlow.emit(energyUpdate)
        runCurrent()

        // Then
        val currentState = stateManager.state.value
        assertEquals(0.75f, currentState.energyBatteryLevel)
        assertEquals(true, currentState.energyIsCharging)
        assertEquals(0f, currentState.energyThermalStress)
    }

    @Test
    fun `should set thermal stress to 1 when power save is active`() = runTest {
        // Given
        val stateManager = MoriStateManager(mockRegistry, backgroundScope)
        val energyUpdate = StateUpdate.Energy(
            batteryLevel = 0.1f,
            isCharging = false,
            isPowerSaveMode = true
        )

        // When
        providerFlow.emit(energyUpdate)
        runCurrent()

        // Then
        assertEquals(1f, stateManager.state.value.energyThermalStress)
    }
}
