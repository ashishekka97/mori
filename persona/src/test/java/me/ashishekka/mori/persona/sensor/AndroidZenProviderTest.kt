package me.ashishekka.mori.persona.sensor

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AndroidZenProviderTest {

    @Test
    fun `start should emit initial zen state`() = runTest {
        // Given
        val mockContext = mockk<Context>(relaxed = true)
        val mockNotificationManager = mockk<NotificationManager> {
            every { currentInterruptionFilter } returns NotificationManager.INTERRUPTION_FILTER_NONE
        }
        val mockAudioManager = mockk<AudioManager> {
            every { ringerMode } returns AudioManager.RINGER_MODE_SILENT
            every { isMusicActive } returns true
        }

        every { mockContext.getSystemService(Context.NOTIFICATION_SERVICE) } returns mockNotificationManager
        every { mockContext.getSystemService(Context.AUDIO_SERVICE) } returns mockAudioManager

        val provider = AndroidZenProvider(mockContext)

        // When
        provider.data.test {
            provider.start()

            // Then
            val state = awaitItem() as StateUpdate.Zen
            assertEquals(true, state.isDndActive) // FILTER_NONE means DND is on
            assertEquals(true, state.isMusicActive)
            assertEquals(AudioManager.RINGER_MODE_SILENT, state.ringerMode)
        }
    }
}
