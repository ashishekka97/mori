package me.ashishekka.mori.persona.sensor

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Android implementation of the Zen Provider (Grade A - Passive).
 * Monitors DND status, ringer mode, and media activity via system signals.
 */
class AndroidZenProvider(
    private val context: Context
) : StateProvider {

    override val energyRating = EnergyRating.GRADE_A
    private val _data = MutableSharedFlow<StateUpdate>(replay = 1)
    override val data = _data.asSharedFlow()

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            emitCurrentState()
        }
    }

    override fun start() {
        val filter = IntentFilter().apply {
            addAction(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED)
            addAction(AudioManager.RINGER_MODE_CHANGED_ACTION)
            addAction(Intent.ACTION_TIME_TICK) // Periodic refresh for isMusicActive
        }
        context.registerReceiver(receiver, filter)
        emitCurrentState()
    }

    override fun stop() {
        try {
            context.unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            // Receiver not registered
        }
    }

    private fun emitCurrentState() {
        val interruptionFilter = notificationManager.currentInterruptionFilter
        val isDndActive = interruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL
        
        val ringerMode = audioManager.ringerMode
        val isMusicActive = audioManager.isMusicActive

        _data.tryEmit(
            StateUpdate.Zen(
                isDndActive = isDndActive,
                isMusicActive = isMusicActive,
                ringerMode = ringerMode
            )
        )
    }
}
