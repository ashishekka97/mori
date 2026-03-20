package me.ashishekka.mori.persona.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AndroidVitalityProviderTest {

    private lateinit var mockContext: Context
    private lateinit var mockSensorManager: SensorManager
    private lateinit var mockStepSensor: Sensor

    @Before
    fun setUp() {
        mockContext = mockk(relaxed = true)
        mockSensorManager = mockk(relaxed = true)
        mockStepSensor = mockk(relaxed = true) {
            every { type } returns Sensor.TYPE_STEP_COUNTER
        }

        every { mockContext.getSystemService(Context.SENSOR_SERVICE) } returns mockSensorManager
        every { mockSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) } returns mockStepSensor
    }

    @Test
    fun `onSensorChanged should calculate progress based on initial reading`() = runTest {
        // Given
        val provider = AndroidVitalityProvider(mockContext)
        
        provider.data.test {
            // First reading (Baseline)
            val firstEvent = createMockStepEvent(1000f)
            provider.onSensorChanged(firstEvent)
            
            val firstItem = awaitItem() as StateUpdate.Vitality
            assertEquals(0f, firstItem.stepsProgress, 0.01f)

            // Second reading (+1000 steps)
            val secondEvent = createMockStepEvent(2000f)
            provider.onSensorChanged(secondEvent)
            
            val secondItem = awaitItem() as StateUpdate.Vitality
            // 1000 steps / 10000 goal = 0.1
            assertEquals(0.1f, secondItem.stepsProgress, 0.01f)
        }
    }

    private fun createMockStepEvent(steps: Float): SensorEvent {
        val mockEvent = mockk<SensorEvent>()
        val valuesField = SensorEvent::class.java.getField("values")
        valuesField.isAccessible = true
        valuesField.set(mockEvent, floatArrayOf(steps))
        
        val sensorField = SensorEvent::class.java.getField("sensor")
        sensorField.isAccessible = true
        sensorField.set(mockEvent, mockStepSensor)
        
        return mockEvent
    }
}
