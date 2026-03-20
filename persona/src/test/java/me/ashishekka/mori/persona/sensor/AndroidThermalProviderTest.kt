package me.ashishekka.mori.persona.sensor

import android.content.Context
import android.os.Build
import android.os.PowerManager
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AndroidThermalProviderTest {

    private lateinit var mockBuildVersionProvider: BuildVersionProvider
    private lateinit var mockContext: Context
    private lateinit var mockPowerManager: PowerManager

    @Before
    fun setUp() {
        mockBuildVersionProvider = mockk()
        mockContext = mockk(relaxed = true)
        mockPowerManager = mockk(relaxed = true)
        every { mockContext.getSystemService(Context.POWER_SERVICE) } returns mockPowerManager
    }

    @Test
    fun `start should emit initial thermal state when API is Q or higher`() = runTest {
        // Given
        every { mockBuildVersionProvider.sdkInt } returns Build.VERSION_CODES.Q
        every { mockPowerManager.currentThermalStatus } returns PowerManager.THERMAL_STATUS_SEVERE

        val provider = AndroidThermalProvider(mockContext, mockBuildVersionProvider)

        // When
        provider.data.test {
            provider.start()

            // Then
            val item = awaitItem()
            if (item is StateUpdate.Thermal) {
                assertEquals(0.6f, item.stressLevel, 0.01f)
            } else {
                throw AssertionError("Expected StateUpdate.Thermal but got $item")
            }
        }
    }

    @Test
    fun `should fallback to 0_0 stress on older API levels`() = runTest {
        // Given
        every { mockBuildVersionProvider.sdkInt } returns Build.VERSION_CODES.M
        val provider = AndroidThermalProvider(mockContext, mockBuildVersionProvider)

        // When
        provider.data.test {
            provider.start()

            // Then
            val item = awaitItem()
            if (item is StateUpdate.Thermal) {
                assertEquals(0.0f, item.stressLevel, 0.01f)
            } else {
                throw AssertionError("Expected StateUpdate.Thermal but got $item")
            }
        }
    }

    @Test
    fun `onThermalStatusChanged should emit new stress level`() = runTest {
        // Given
        every { mockBuildVersionProvider.sdkInt } returns Build.VERSION_CODES.Q
        val provider = AndroidThermalProvider(mockContext, mockBuildVersionProvider)

        provider.data.test {
            provider.start()
            awaitItem() // Consume initial

            // When
            provider.onThermalStatusChanged(PowerManager.THERMAL_STATUS_CRITICAL)

            // Then
            val item = awaitItem()
            if (item is StateUpdate.Thermal) {
                assertEquals(1.0f, item.stressLevel, 0.01f)
            } else {
                throw AssertionError("Expected StateUpdate.Thermal but got $item")
            }
        }
    }
}
