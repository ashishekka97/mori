package me.ashishekka.mori.persona.sensor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class AndroidLunarProviderTest {

    private val mockContext = mockk<Context>(relaxed = true)
    private val mockCalculator = mockk<LunarCalculator>()
    private val provider = AndroidLunarProvider(mockContext, mockCalculator)

    @Test
    fun `start should emit initial moon phase and register receiver`() = runTest {
        // Given
        every { mockCalculator.calculateNormalizedPhase(any()) } returns 0.5f

        // When
        provider.start()

        // Then
        val firstUpdate = provider.data.first()
        assertTrue(firstUpdate is StateUpdate.Lunar)
        assertEquals(0.5f, (firstUpdate as StateUpdate.Lunar).phase)
        verify { mockContext.registerReceiver(any(), any()) }
    }

    @Test
    fun `receiver should trigger re-calculation on day change`() = runTest {
        // Capture receiver
        val receiverSlot = slot<BroadcastReceiver>()
        every { mockCalculator.calculateNormalizedPhase(any()) } returns 0.2f

        provider.start()
        verify { mockContext.registerReceiver(capture(receiverSlot), any()) }
        val receiver = receiverSlot.captured

        // Simulate a day change
        val mockIntent = mockk<Intent>()
        every { mockIntent.action } returns Intent.ACTION_TIMEZONE_CHANGED

        every { mockCalculator.calculateNormalizedPhase(any()) } returns 0.8f

        // When
        receiver.onReceive(mockContext, mockIntent)

        // Then
        val latestUpdate = provider.data.first()
        assertEquals(0.8f, (latestUpdate as StateUpdate.Lunar).phase)
    }

    private fun assertTrue(condition: Boolean) {
        org.junit.Assert.assertTrue(condition)
    }
}
