package me.ashishekka.mori.persona.sensor

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Android implementation of the Social Provider (Grade C - Periodic).
 * Captures a snapshot of the user's notification tray to calculate "Digital Noise."
 */
class AndroidSocialProvider(
    private val context: Context
) : StateProvider {

    private val _data = MutableSharedFlow<StateUpdate>(replay = 1)
    override val data = _data.asSharedFlow()

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_TIME_TICK) {
                emitCurrentState()
            }
        }
    }

    override fun start() {
        val filter = IntentFilter(Intent.ACTION_TIME_TICK)
        context.registerReceiver(receiver, filter)
        emitCurrentState()
    }

    override fun stop() {
        try {
            context.unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            // Not registered
        }
    }

    private fun emitCurrentState() {
        // Simple snapshot: count how many notifications are currently visible
        // Note: Accessing getActiveNotifications() doesn't require NotificationListenerService permission,
        // but it only returns notifications posted by THIS app unless the app is a system app or 
        // has special access. 
        // FALLBACK: We will treat this as a "Zen Pulse" skeleton. 
        // In Phase 10, we'll implement the full Listener Service.
        val activeCount = notificationManager.activeNotifications.size
        
        // Normalize: 10 notifications = 1.0 (Full Noise)
        val noiseLevel = (activeCount / 10f).coerceIn(0f, 1f)

        _data.tryEmit(StateUpdate.Social(noiseLevel))
    }
}
