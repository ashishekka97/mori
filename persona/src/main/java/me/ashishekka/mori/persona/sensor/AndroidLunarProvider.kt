package me.ashishekka.mori.persona.sensor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.Calendar

/**
 * Android implementation of the Lunar Provider (Grade A - Passive).
 * Calculates the current moon phase once per day or on system time changes.
 */
class AndroidLunarProvider(
    private val context: Context,
    private val calculator: LunarCalculator
) : StateProvider {

    private val _data = MutableSharedFlow<StateUpdate>(replay = 1)
    override val data = _data.asSharedFlow()

    private var lastCalculationDay = -1

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val now = Calendar.getInstance()
            val currentDay = now.get(Calendar.DAY_OF_YEAR)
            
            // Only recalculate if the day has changed or system time was manually adjusted
            if (currentDay != lastCalculationDay || intent.action != Intent.ACTION_TIME_TICK) {
                emitCurrentState(now)
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
        emitCurrentState(Calendar.getInstance())
    }

    override fun stop() {
        try {
            context.unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            // Not registered
        }
    }

    private fun emitCurrentState(now: Calendar) {
        lastCalculationDay = now.get(Calendar.DAY_OF_YEAR)
        val phase = calculator.calculateNormalizedPhase(now)
        _data.tryEmit(StateUpdate.Lunar(phase))
    }
}
