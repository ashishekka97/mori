package me.ashishekka.mori.persona.sensor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.PowerManager
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AndroidEnergyProviderTest {

    @Test
    fun `start should emit initial energy state`() = runTest {
        // Given
        val mockContext = mockk<Context>(relaxed = true)
        val mockPowerManager = mockk<PowerManager> {
            every { isPowerSaveMode } returns true
        }

        every { mockContext.getSystemService(Context.POWER_SERVICE) } returns mockPowerManager

        val mockBatteryIntent = mockk<Intent> {
            every { getIntExtra(BatteryManager.EXTRA_LEVEL, -1) } returns 50
            every { getIntExtra(BatteryManager.EXTRA_SCALE, -1) } returns 100
            every { getIntExtra(BatteryManager.EXTRA_STATUS, -1) } returns BatteryManager.BATTERY_STATUS_CHARGING
        }
        
        // Mock the immediate battery status query via registerReceiver(null, filter)
        every { mockContext.registerReceiver(null, any()) } returns mockBatteryIntent

        val provider = AndroidEnergyProvider(mockContext)

        // When
        provider.data.test {
            provider.start()

            // Then
            val state = awaitItem() as StateUpdate.Energy
            assertEquals(0.5f, state.batteryLevel, 0.01f)
            assertEquals(true, state.isCharging)
            assertEquals(true, state.isPowerSaveMode)
        }
    }

    @Test
    fun `broadcast should trigger new state emission`() = runTest {
        // Given
        val mockContext = mockk<Context>(relaxed = true)
        val mockPowerManager = mockk<PowerManager> {
            every { isPowerSaveMode } returns false
        }

        every { mockContext.getSystemService(Context.POWER_SERVICE) } returns mockPowerManager

        // The Intent returned when queried
        val mockBatteryIntent = mockk<Intent> {
            every { getIntExtra(BatteryManager.EXTRA_LEVEL, -1) } returns 10
            every { getIntExtra(BatteryManager.EXTRA_SCALE, -1) } returns 100
            every { getIntExtra(BatteryManager.EXTRA_STATUS, -1) } returns BatteryManager.BATTERY_STATUS_DISCHARGING
        }
        every { mockContext.registerReceiver(null, any()) } returns mockBatteryIntent

        val receiverSlot = slot<BroadcastReceiver>()
        every { mockContext.registerReceiver(capture(receiverSlot), any()) } returns mockBatteryIntent

        val provider = AndroidEnergyProvider(mockContext)

        provider.data.test {
            provider.start()
            
            // Consume initial
            awaitItem()

            // When: Broadcast is received
            val broadcastIntent = mockk<Intent> {
                every { action } returns Intent.ACTION_BATTERY_CHANGED
            }
            receiverSlot.captured.onReceive(mockContext, broadcastIntent)

            // Then: Should emit new state based on current query
            val newState = awaitItem() as StateUpdate.Energy
            assertEquals(0.1f, newState.batteryLevel, 0.01f)
            assertEquals(false, newState.isCharging)
            assertEquals(false, newState.isPowerSaveMode)
        }
    }
}
