package me.ashishekka.mori.persona.sensor

import android.app.NotificationManager
import android.content.Context
import android.service.notification.StatusBarNotification
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AndroidSocialProviderTest {

    @Test
    fun `start should emit initial social noise level excluding groups`() = runTest {
        // Given
        val mockContext = mockk<Context>(relaxed = true)
        val mockNotificationManager = mockk<NotificationManager>()
        
        // Mock 3 individual notifications and 1 summary group
        val individual1 = mockk<StatusBarNotification> { every { isGroup } returns false }
        val individual2 = mockk<StatusBarNotification> { every { isGroup } returns false }
        val individual3 = mockk<StatusBarNotification> { every { isGroup } returns false }
        val summary = mockk<StatusBarNotification> { every { isGroup } returns true }
        
        val mockNotifications = arrayOf(individual1, individual2, individual3, summary)
        every { mockNotificationManager.activeNotifications } returns mockNotifications
        every { mockContext.getSystemService(Context.NOTIFICATION_SERVICE) } returns mockNotificationManager

        val provider = AndroidSocialProvider(mockContext)

        // When
        provider.data.test {
            provider.start()

            // Then
            val state = awaitItem() as StateUpdate.Social
            // Should count only the 3 individuals: 3 / 10 = 0.3
            assertEquals(0.3f, state.noiseLevel, 0.01f)
        }
    }

    @Test
    fun `noiseLevel should cap at 1_0`() = runTest {
        // Given
        val mockContext = mockk<Context>(relaxed = true)
        val mockNotificationManager = mockk<NotificationManager>()
        
        // Mock 15 active notifications
        val mockNotifications = Array(15) { mockk<StatusBarNotification> { every { isGroup } returns false } }
        every { mockNotificationManager.activeNotifications } returns mockNotifications
        every { mockContext.getSystemService(Context.NOTIFICATION_SERVICE) } returns mockNotificationManager

        val provider = AndroidSocialProvider(mockContext)

        // When
        provider.data.test {
            provider.start()

            // Then
            val state = awaitItem() as StateUpdate.Social
            assertEquals(1.0f, state.noiseLevel, 0.01f)
        }
    }
}
