package me.ashishekka.mori.persona.sensor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.PowerManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Android implementation of the Energy Provider (Grade A - Passive).
 * Listens to system broadcasts for battery changes and power save mode toggles.
 */
class AndroidEnergyProvider(
    private val context: Context
) : StateProvider {

    private val _data = MutableSharedFlow<StateUpdate>(replay = 1)
    override val data = _data.asSharedFlow()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_BATTERY_CHANGED,
                PowerManager.ACTION_POWER_SAVE_MODE_CHANGED -> {
                    emitCurrentState()
                }
            }
        }
    }

    override fun start() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        }
        context.registerReceiver(receiver, filter)
        // Emit initial state immediately
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
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
            context.registerReceiver(null, filter)
        }

        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = if (level != -1 && scale != -1) level / scale.toFloat() else 1f

        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val isPowerSave = powerManager.isPowerSaveMode

        _data.tryEmit(
            StateUpdate.Energy(
                batteryLevel = batteryPct,
                isCharging = isCharging,
                isPowerSaveMode = isPowerSave
            )
        )
    }
}
