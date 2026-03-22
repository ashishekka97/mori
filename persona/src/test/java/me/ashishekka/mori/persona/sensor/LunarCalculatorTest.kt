package me.ashishekka.mori.persona.sensor

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class LunarCalculatorTest {

    private val calculator = LunarCalculator()

    @Test
    fun `calculateNormalizedPhase should return values between 0 and 1`() {
        val calendar = Calendar.getInstance()

        // Test various dates across the year
        for (month in 0..11) {
            for (day in 1..28) {
                calendar.set(2024, month, day)
                val phase = calculator.calculateNormalizedPhase(calendar)
                assert(phase in 0f..1f)
            }
        }
    }

    @Test
    fun `calculateNormalizedPhase should return approx 0 for a known New Moon`() {
        // Feb 9, 2024 was a New Moon
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.FEBRUARY, 9)
        }
        val phase = calculator.calculateNormalizedPhase(calendar)

        // Conway's algorithm is approximate, so we check for proximity to 0 or 1
        // For Feb 9, 2024 it returns ~0.03
        assertEquals(0.03f, phase, 0.05f)
    }

    @Test
    fun `calculateNormalizedPhase should return approx 0-5 for a known Full Moon`() {
        // Feb 24, 2024 was a Full Moon
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.FEBRUARY, 24)
        }
        val phase = calculator.calculateNormalizedPhase(calendar)

        // For Feb 24, 2024 it returns ~0.53
        assertEquals(0.53f, phase, 0.05f)
    }
}
