package me.ashishekka.mori.persona.sensor

import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Android implementation of the Thermal Provider (Grade B - Gated/Reactive).
 * Monitors system thermal stress levels to trigger protective rendering modes.
 */
class AndroidThermalProvider(
    private val context: Context,
    private val buildVersionProvider: BuildVersionProvider = DefaultBuildVersionProvider()
) : StateProvider, PowerManager.OnThermalStatusChangedListener {

    private val _data = MutableSharedFlow<StateUpdate>(replay = 1)
    override val data = _data.asSharedFlow()

    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    override fun start() {
        if (buildVersionProvider.sdkInt >= Build.VERSION_CODES.Q) {
            powerManager.addThermalStatusListener(this)
            emitCurrentState(powerManager.currentThermalStatus)
        } else {
            // Fallback for older devices: always assume "Cool"
            _data.tryEmit(StateUpdate.Thermal(0.0f))
        }
    }

    override fun stop() {
        if (buildVersionProvider.sdkInt >= Build.VERSION_CODES.Q) {
            powerManager.removeThermalStatusListener(this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onThermalStatusChanged(status: Int) {
        emitCurrentState(status)
    }

    private fun emitCurrentState(status: Int) {
        val stressLevel = when (status) {
            PowerManager.THERMAL_STATUS_NONE,
            PowerManager.THERMAL_STATUS_LIGHT -> 0.0f
            
            PowerManager.THERMAL_STATUS_MODERATE -> 0.3f
            
            PowerManager.THERMAL_STATUS_SEVERE -> 0.6f
            
            PowerManager.THERMAL_STATUS_CRITICAL,
            PowerManager.THERMAL_STATUS_EMERGENCY,
            PowerManager.THERMAL_STATUS_SHUTDOWN -> 1.0f
            
            else -> 0.0f
        }
        _data.tryEmit(StateUpdate.Thermal(stressLevel))
    }
}
