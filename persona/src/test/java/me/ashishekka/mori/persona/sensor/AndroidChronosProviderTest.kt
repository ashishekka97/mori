package me.ashishekka.mori.persona.sensor

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class AndroidChronosProviderTest {

    @Test
    fun `start should emit initial day progress`() = runTest {
        // Given
        val mockContext = mockk<Context>(relaxed = true)
        val mockAlarmManager = mockk<AlarmManager> {
            every { nextAlarmClock } returns null
        }
        every { mockContext.getSystemService(Context.ALARM_SERVICE) } returns mockAlarmManager

        val provider = AndroidChronosProvider(mockContext)

        // When
        provider.data.test {
            provider.start()

            // Then
            val state = awaitItem() as StateUpdate.Chronos
            
            // Verify normalization (Calculation check)
            val calendar = Calendar.getInstance()
            val expected = ((calendar.get(Calendar.HOUR_OF_DAY) * 3600) + 
                           (calendar.get(Calendar.MINUTE) * 60) + 
                           calendar.get(Calendar.SECOND)).toFloat() / (24 * 3600)
            
            assertEquals(expected, state.dayProgress, 0.01f)
        }
    }

    @Test
    fun `TIME_TICK broadcast should trigger new emission`() = runTest {
        // Given
        val mockContext = mockk<Context>(relaxed = true)
        val mockAlarmManager = mockk<AlarmManager> {
            every { nextAlarmClock } returns null
        }
        every { mockContext.getSystemService(Context.ALARM_SERVICE) } returns mockAlarmManager

        val receiverSlot = slot<BroadcastReceiver>()
        every { mockContext.registerReceiver(capture(receiverSlot), any()) } returns null

        val provider = AndroidChronosProvider(mockContext)

        provider.data.test {
            provider.start()
            awaitItem() // Consume initial

            // When
            val intent = mockk<Intent> {
                every { action } returns Intent.ACTION_TIME_TICK
            }
            receiverSlot.captured.onReceive(mockContext, intent)

            // Then
            awaitItem() // Should emit again
        }
    }
}
