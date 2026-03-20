package me.ashishekka.mori.persona.sensor

import android.content.Context
import android.os.PowerManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Android implementation of the Thermal Provider (Grade B - Gated/Reactive).
 * Monitors system thermal stress levels to trigger protective rendering modes.
 */
class AndroidThermalProvider(
    private val context: Context,
    private val listenerProvider: ThermalListenerProvider = DefaultThermalListenerProvider()
) : StateProvider {

    override val energyRating = EnergyRating.GRADE_B
    private val _data = MutableSharedFlow<StateUpdate>(replay = 1)
    override val data = _data.asSharedFlow()

    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    private val thermalListener = PowerManager.OnThermalStatusChangedListener { status ->
        emitCurrentState(status)
    }

    override fun start() {
        listenerProvider.register(powerManager, thermalListener)
        emitCurrentState(listenerProvider.getCurrentStatus(powerManager))
    }

    override fun stop() {
        listenerProvider.unregister(powerManager, thermalListener)
    }

    /**
     * Internal helper to test status changes.
     */
    internal fun handleStatusChanged(status: Int) {
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
