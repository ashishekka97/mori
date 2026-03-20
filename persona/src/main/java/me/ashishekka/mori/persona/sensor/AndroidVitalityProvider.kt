package me.ashishekka.mori.persona.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Android implementation of the Vitality Provider (Grade B - Gated).
 * Uses the Step Counter sensor to drive the "Vitality" layers of the wallpaper.
 * Requires ACTIVITY_RECOGNITION permission.
 */
class AndroidVitalityProvider(
    private val context: Context
) : StateProvider, SensorEventListener {

    private val _data = MutableSharedFlow<StateUpdate>(replay = 1)
    override val data = _data.asSharedFlow()

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    // Local state to handle sensor resetting on reboot.
    // In a real app, we'd persist the start-of-day value in a database.
    private var initialStepCount: Float = -1f
    private val dailyStepGoal: Float = 10000f

    override fun start() {
        if (stepSensor == null) {
            // Fallback for devices without step counter: 0 progress
            _data.tryEmit(StateUpdate.Vitality(stepsProgress = 0f))
            return
        }
        
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalStepsSinceReboot = event.values[0]
            
            if (initialStepCount < 0) {
                initialStepCount = totalStepsSinceReboot
            }

            val currentDaySteps = totalStepsSinceReboot - initialStepCount
            val progress = (currentDaySteps / dailyStepGoal).coerceIn(0f, 1f)

            _data.tryEmit(
                StateUpdate.Vitality(
                    stepsProgress = progress,
                    sleepClarity = 1.0f, // Default until Health Connect Phase
                    standGoalProgress = 0.0f // Default until Health Connect Phase
                )
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }
}
