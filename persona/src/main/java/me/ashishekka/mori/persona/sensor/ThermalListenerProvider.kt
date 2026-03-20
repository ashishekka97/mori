package me.ashishekka.mori.persona.sensor

import android.os.Build
import android.os.PowerManager

/**
 * Strategy interface to handle thermal listener registration.
 * Abstracted to support MinSDK 28 while utilizing API 29+ features.
 */
interface ThermalListenerProvider {
    fun register(powerManager: PowerManager, listener: PowerManager.OnThermalStatusChangedListener)
    fun unregister(powerManager: PowerManager, listener: PowerManager.OnThermalStatusChangedListener)
    fun getCurrentStatus(powerManager: PowerManager): Int
}

class DefaultThermalListenerProvider : ThermalListenerProvider {
    override fun register(powerManager: PowerManager, listener: PowerManager.OnThermalStatusChangedListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            powerManager.addThermalStatusListener(listener)
        }
    }

    override fun unregister(powerManager: PowerManager, listener: PowerManager.OnThermalStatusChangedListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            powerManager.removeThermalStatusListener(listener)
        }
    }

    override fun getCurrentStatus(powerManager: PowerManager): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            powerManager.currentThermalStatus
        } else {
            PowerManager.THERMAL_STATUS_NONE
        }
    }
}
