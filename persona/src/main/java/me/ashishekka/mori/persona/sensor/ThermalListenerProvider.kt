package me.ashishekka.mori.persona.sensor

import android.annotation.SuppressLint
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

class DefaultThermalListenerProvider(
    private val buildVersionProvider: BuildVersionProvider = DefaultBuildVersionProvider()
) : ThermalListenerProvider {
    
    @SuppressLint("NewApi")
    override fun register(powerManager: PowerManager, listener: PowerManager.OnThermalStatusChangedListener) {
        if (buildVersionProvider.sdkInt >= Build.VERSION_CODES.Q) {
            powerManager.addThermalStatusListener(listener)
        }
    }

    @SuppressLint("NewApi")
    override fun unregister(powerManager: PowerManager, listener: PowerManager.OnThermalStatusChangedListener) {
        if (buildVersionProvider.sdkInt >= Build.VERSION_CODES.Q) {
            powerManager.removeThermalStatusListener(listener)
        }
    }

    @SuppressLint("NewApi")
    override fun getCurrentStatus(powerManager: PowerManager): Int {
        return if (buildVersionProvider.sdkInt >= Build.VERSION_CODES.Q) {
            powerManager.currentThermalStatus
        } else {
            PowerManager.THERMAL_STATUS_NONE
        }
    }
}
