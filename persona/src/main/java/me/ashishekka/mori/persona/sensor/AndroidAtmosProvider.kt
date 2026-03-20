package me.ashishekka.mori.persona.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.math.log10

/**
 * Android implementation of the Atmos Provider (Grade B - Gated).
 * Uses the Light sensor in a "Burst" mode: activates for 2 seconds on visibility,
 * then shuts down to save battery.
 */
class AndroidAtmosProvider(
    private val context: Context,
    private val scope: CoroutineScope
) : StateProvider, SensorEventListener {

    override val energyRating = EnergyRating.GRADE_B
    private val _data = MutableSharedFlow<StateUpdate>(replay = 1)
    override val data = _data.asSharedFlow()

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    private var burstJob: Job? = null

    override fun start() {
        if (lightSensor == null) return
        
        // Register the listener
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)

        // Start the "Burst" timer to auto-stop after 2 seconds
        burstJob?.cancel()
        burstJob = scope.launch {
            delay(2000)
            stop()
        }
    }

    override fun stop() {
        sensorManager.unregisterListener(this)
        burstJob?.cancel()
        burstJob = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val lux = event.values[0]
            
            // Logarithmic normalization: human eye perceives light logarithmically.
            // 1 lux (Moonlight) -> ~0.0
            // 100,000 lux (Direct Sun) -> ~1.0
            // log10(1) = 0, log10(100,000) = 5
            val normalized = (log10(lux.coerceAtLeast(1f)) / 5.0f).coerceIn(0f, 1f)
            
            _data.tryEmit(StateUpdate.Atmos(normalized))
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }
}
