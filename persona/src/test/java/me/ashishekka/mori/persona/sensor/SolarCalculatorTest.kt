package me.ashishekka.mori.persona.sensor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class SolarCalculatorTest {

    private val calculator = SolarCalculator()

    @Test
    fun `solar noon should result in altitude close to 1_0 (normalized) at Equator during Equinox`() {
        // Given: Vernal Equinox at the Equator (Sun is directly overhead at noon)
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.MARCH, 21, 12, 0, 0)
        }

        val altitude = calculator.calculateNormalizedAltitude(calendar, 0.0)

        // At the equator on equinox, noon is 90 degrees (1.0 normalized)
        assertEquals(1.0f, altitude, 0.05f)
    }

    @Test
    fun `midnight should result in negative altitude at Equator`() {
        // Given: Equator at midnight
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.MARCH, 21, 0, 0, 0)
        }

        val altitude = calculator.calculateNormalizedAltitude(calendar, 0.0)

        // Should be negative (sun is below horizon)
        assertTrue("Altitude should be negative at midnight", altitude < 0f)
        assertEquals(-1.0f, altitude, 0.05f)
    }

    @Test
    fun `North Pole during Summer Solstice should have constant daylight`() {
        // Given: North Pole (90.0 latitude) on June 21
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.JUNE, 21, 0, 0, 0)
        }

        // Midnight at North Pole during Summer Solstice
        val midnightAltitude = calculator.calculateNormalizedAltitude(calendar, 90.0)
        assertTrue("Sun should be above horizon at midnight in Arctic Summer", midnightAltitude > 0f)

        // Noon at North Pole during Summer Solstice
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        val noonAltitude = calculator.calculateNormalizedAltitude(calendar, 90.0)
        assertTrue("Sun should be above horizon at noon in Arctic Summer", noonAltitude > 0f)

        // At 90 deg lat, declination is ~23.45. Altitude = asin(sin(90)*sin(23.45) + 0) = 23.45 degrees.
        // 23.45 / 90 = 0.26
        assertEquals(0.26f, noonAltitude, 0.05f)
        assertEquals(midnightAltitude, noonAltitude, 0.01f) // At the exact pole, altitude is constant throughout the day
    }

    @Test
    fun `North Pole during Winter Solstice should have constant darkness`() {
        // Given: North Pole (90.0 latitude) on December 21
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.DECEMBER, 21, 12, 0, 0)
        }

        val noonAltitude = calculator.calculateNormalizedAltitude(calendar, 90.0)
        assertTrue("Sun should be below horizon at noon in Arctic Winter", noonAltitude < 0f)

        // Altitude should be ~ -23.45 degrees -> -0.26 normalized
        assertEquals(-0.26f, noonAltitude, 0.05f)
    }

    @Test
    fun `Equator during Solstice should still have day and night`() {
        // June 21 at Equator
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.JUNE, 21, 12, 0, 0)
        }

        val noonAltitude = calculator.calculateNormalizedAltitude(calendar, 0.0)
        // At equator, max altitude is 90 - 23.45 = 66.55 degrees -> 0.74 normalized
        assertEquals(0.74f, noonAltitude, 0.05f)

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        val midnightAltitude = calculator.calculateNormalizedAltitude(calendar, 0.0)
        assertTrue("Should be night at Equator during Solstice", midnightAltitude < 0f)
    }
}
