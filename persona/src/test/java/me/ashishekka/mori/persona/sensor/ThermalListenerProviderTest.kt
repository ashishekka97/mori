package me.ashishekka.mori.persona.sensor

import android.os.Build
import android.os.PowerManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ThermalListenerProviderTest {

    private lateinit var mockBuildVersionProvider: BuildVersionProvider
    private lateinit var provider: DefaultThermalListenerProvider
    private val mockPowerManager = mockk<PowerManager>(relaxed = true)
    private val mockListener = mockk<PowerManager.OnThermalStatusChangedListener>(relaxed = true)

    @Before
    fun setUp() {
        mockBuildVersionProvider = mockk()
        provider = DefaultThermalListenerProvider(mockBuildVersionProvider)
    }

    @Test
    fun `register should call powerManager on API 29+`() {
        // Given
        every { mockBuildVersionProvider.sdkInt } returns Build.VERSION_CODES.Q

        // When
        provider.register(mockPowerManager, mockListener)

        // Then
        verify { mockPowerManager.addThermalStatusListener(mockListener) }
    }

    @Test
    fun `register should not call powerManager on API 28`() {
        // Given
        every { mockBuildVersionProvider.sdkInt } returns Build.VERSION_CODES.P

        // When
        provider.register(mockPowerManager, mockListener)

        // Then
        verify(exactly = 0) { mockPowerManager.addThermalStatusListener(any()) }
    }

    @Test
    fun `unregister should call powerManager on API 29+`() {
        // Given
        every { mockBuildVersionProvider.sdkInt } returns Build.VERSION_CODES.Q

        // When
        provider.unregister(mockPowerManager, mockListener)

        // Then
        verify { mockPowerManager.removeThermalStatusListener(mockListener) }
    }

    @Test
    fun `getCurrentStatus should return status on API 29+`() {
        // Given
        every { mockBuildVersionProvider.sdkInt } returns Build.VERSION_CODES.Q
        every { mockPowerManager.currentThermalStatus } returns PowerManager.THERMAL_STATUS_CRITICAL

        // When
        val status = provider.getCurrentStatus(mockPowerManager)

        // Then
        assertEquals(PowerManager.THERMAL_STATUS_CRITICAL, status)
    }

    @Test
    fun `getCurrentStatus should return NONE on API 28`() {
        // Given
        every { mockBuildVersionProvider.sdkInt } returns Build.VERSION_CODES.P

        // When
        val status = provider.getCurrentStatus(mockPowerManager)

        // Then
        assertEquals(PowerManager.THERMAL_STATUS_NONE, status)
    }
}
