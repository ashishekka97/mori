package me.ashishekka.mori.persona.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AndroidAtmosProviderTest {

    private lateinit var mockContext: Context
    private lateinit var mockSensorManager: SensorManager
    private lateinit var mockLightSensor: Sensor
    private val testScope = TestScope(UnconfinedTestDispatcher())

    @Before
    fun setUp() {
        mockContext = mockk(relaxed = true)
        mockSensorManager = mockk(relaxed = true)
        mockLightSensor = mockk(relaxed = true) {
            every { type } returns Sensor.TYPE_LIGHT
        }

        every { mockContext.getSystemService(Context.SENSOR_SERVICE) } returns mockSensorManager
        every { mockSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) } returns mockLightSensor
    }

    @Test
    fun `start should register listener and stop after delay`() = runTest {
        // Given
        val provider = AndroidAtmosProvider(mockContext, this)

        // When
        provider.start()

        // Then
        verify { mockSensorManager.registerListener(any(), mockLightSensor, any()) }
        
        // Advance time to trigger burst auto-stop
        advanceTimeBy(2001)
        
        verify { mockSensorManager.unregisterListener(any<AndroidAtmosProvider>()) }
    }

    @Test
    fun `onSensorChanged should emit normalized lux`() = runTest {
        // Given
        val provider = AndroidAtmosProvider(mockContext, testScope)
        
        provider.data.test {
            // Mock a sensor event (100 lux)
            val mockEvent = mockk<SensorEvent>()
            val valuesField = SensorEvent::class.java.getField("values")
            valuesField.isAccessible = true
            valuesField.set(mockEvent, floatArrayOf(100f))
            val sensorField = SensorEvent::class.java.getField("sensor")
            sensorField.isAccessible = true
            sensorField.set(mockEvent, mockLightSensor)

            // When
            provider.onSensorChanged(mockEvent)

            // Then
            val state = awaitItem() as StateUpdate.Atmos
            // log10(100) / 5.0 = 2 / 5 = 0.4
            assertEquals(0.4f, state.lightLevel, 0.01f)
        }
    }
}
