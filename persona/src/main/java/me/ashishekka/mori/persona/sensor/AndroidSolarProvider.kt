package me.ashishekka.mori.persona.sensor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.Calendar

/**
 * Android implementation of the Solar Provider (Grade A/B - Math-Heavy).
 * Triggers on time changes and calculates sun altitude.
 */
class AndroidSolarProvider(
    private val context: Context,
    private val calculator: SolarCalculator
) : StateProvider {

    private val _data = MutableSharedFlow<StateUpdate>(replay = 1)
    override val data = _data.asSharedFlow()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            emitCurrentState()
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
            // Not registered
        }
    }

    private fun emitCurrentState() {
        val now = Calendar.getInstance()
        
        // Future Phase: Get actual latitude from Location Services.
        // Current: Fallback to 0.0 (Equator) for privacy-first baseline.
        val defaultLatitude = 0.0 
        
        val altitude = calculator.calculateNormalizedAltitude(now, defaultLatitude)

        _data.tryEmit(StateUpdate.Solar(altitude))
    }
}
