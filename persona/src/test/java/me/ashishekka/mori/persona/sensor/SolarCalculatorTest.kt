package me.ashishekka.mori.persona.sensor

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class SolarCalculatorTest {

    private val calculator = SolarCalculator()

    @Test
    fun `solar noon should result in altitude close to 1_0 (normalized)`() {
        // Given: Vernal Equinox at the Equator (Sun is directly overhead at noon)
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_YEAR, 81) // Approx March 21
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        
        val altitude = calculator.calculateNormalizedAltitude(calendar, 0.0)
        
        // At the equator on equinox, noon is 90 degrees (1.0 normalized)
        assertEquals(1.0f, altitude, 0.01f)
    }

    @Test
    fun `midnight should result in negative altitude`() {
        // Given: Equator at midnight
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
        }
        
        val altitude = calculator.calculateNormalizedAltitude(calendar, 0.0)
        
        // Should be negative (sun is below horizon)
        assertEquals(true, altitude < 0f)
    }

    @Test
    fun `sunset should be close to 0_0 (normalized)`() {
        // Given: Equator, approx 6 PM
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_YEAR, 81)
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
        }
        
        val altitude = calculator.calculateNormalizedAltitude(calendar, 0.0)
        
        // Approx 0.0 (Horizon)
        assertEquals(0.0f, altitude, 0.05f)
    }
}
