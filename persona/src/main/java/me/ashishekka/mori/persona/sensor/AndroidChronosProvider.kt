package me.ashishekka.mori.persona.sensor

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.Calendar

/**
 * Android implementation of the Chronos Provider (Grade A - Passive).
 * Synchronizes with the system clock every minute using ACTION_TIME_TICK.
 */
class AndroidChronosProvider(
    private val context: Context
) : StateProvider {

    private val _data = MutableSharedFlow<StateUpdate>(replay = 1)
    override val data = _data.asSharedFlow()

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_TIME_TICK || 
                intent.action == Intent.ACTION_TIME_CHANGED ||
                intent.action == Intent.ACTION_TIMEZONE_CHANGED) {
                emitCurrentState()
            }
        }
    }

    override fun start() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
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
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        // Normalize day progress (0.0 to 1.0)
        val totalSecondsInDay = 24 * 60 * 60
        val currentSeconds = (hour * 3600) + (minute * 60) + second
        val dayProgress = currentSeconds.toFloat() / totalSecondsInDay

        val nextAlarm = alarmManager.nextAlarmClock?.triggerTime ?: 0L

        _data.tryEmit(
            StateUpdate.Chronos(
                dayProgress = dayProgress,
                nextAlarmTime = nextAlarm
            )
        )
    }
}
