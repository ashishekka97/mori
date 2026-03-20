package me.ashishekka.mori.persona.sensor

import android.content.Context
import android.os.PowerManager
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AndroidThermalProviderTest {

    private lateinit var mockListenerProvider: ThermalListenerProvider
    private lateinit var mockContext: Context
    private lateinit var mockPowerManager: PowerManager

    @Before
    fun setUp() {
        mockListenerProvider = mockk(relaxed = true)
        mockContext = mockk(relaxed = true)
        mockPowerManager = mockk(relaxed = true)
        every { mockContext.getSystemService(Context.POWER_SERVICE) } returns mockPowerManager
    }

    @Test
    fun `start should emit initial thermal state from provider`() = runTest {
        // Given
        every { mockListenerProvider.getCurrentStatus(any()) } returns PowerManager.THERMAL_STATUS_SEVERE

        val provider = AndroidThermalProvider(mockContext, mockListenerProvider)

        // When
        provider.data.test {
            provider.start()

            // Then
            val item = awaitItem() as StateUpdate.Thermal
            assertEquals(0.6f, item.stressLevel, 0.01f)
        }
    }

    @Test
    fun `handleStatusChanged should emit new stress level`() = runTest {
        // Given
        val provider = AndroidThermalProvider(mockContext, mockListenerProvider)

        provider.data.test {
            provider.start()
            awaitItem() // Consume initial

            // When
            provider.handleStatusChanged(PowerManager.THERMAL_STATUS_CRITICAL)

            // Then
            val item = awaitItem() as StateUpdate.Thermal
            assertEquals(1.0f, item.stressLevel, 0.01f)
        }
    }
}
