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
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class AndroidSolarProviderTest {

    private val mockContext = mockk<Context>(relaxed = true)
    private val mockCalculator = mockk<SolarCalculator>()
    private val provider = AndroidSolarProvider(mockContext, mockCalculator)

    @Test
    fun `start should emit initial sun altitude and register receiver`() = runTest {
        // Given
        every { mockCalculator.calculateNormalizedAltitude(any(), any()) } returns 0.75f

        // When
        provider.start()

        // Then
        val firstUpdate = provider.data.first()
        assertTrue("Update should be of type Solar", firstUpdate is StateUpdate.Solar)
        assertEquals(0.75f, (firstUpdate as StateUpdate.Solar).altitude)
        verify { mockContext.registerReceiver(any(), any()) }
    }

    @Test
    fun `receiver should trigger re-calculation on time tick`() = runTest {
        // Capture receiver
        val receiverSlot = slot<BroadcastReceiver>()
        every { mockCalculator.calculateNormalizedAltitude(any(), any()) } returns 0.1f

        provider.start()
        verify { mockContext.registerReceiver(capture(receiverSlot), any()) }
        val receiver = receiverSlot.captured

        // Simulate a time tick
        val mockIntent = mockk<Intent>()
        every { mockIntent.action } returns Intent.ACTION_TIME_TICK

        every { mockCalculator.calculateNormalizedAltitude(any(), any()) } returns -0.5f

        // When
        receiver.onReceive(mockContext, mockIntent)

        // Then
        val latestUpdate = provider.data.first()
        assertEquals(-0.5f, (latestUpdate as StateUpdate.Solar).altitude)
    }
}
