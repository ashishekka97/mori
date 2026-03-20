package me.ashishekka.mori.persona.sensor

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class LunarCalculatorTest {

    private val calculator = LunarCalculator()

    @Test
    fun `full moon should be close to 0_5 (normalized)`() {
        // A known Full Moon: July 21, 2024
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.JULY, 21)
        }
        
        val phase = calculator.calculateNormalizedPhase(calendar)
        
        // Should be near 0.5 (Full Moon)
        // Note: Simple algorithm has +/- 1 day error, so we use a wider tolerance
        assertEquals(0.5f, phase, 0.1f)
    }

    @Test
    fun `new moon should be close to 0_0 or 1_0 (normalized)`() {
        // A known New Moon: July 5, 2024
        val calendar = Calendar.getInstance().apply {
            set(2024, Calendar.JULY, 5)
        }
        
        val phase = calculator.calculateNormalizedPhase(calendar)
        
        // Should be near 0.0 or 1.0 (New Moon)
        // Check if it's within 0.1 of either 0.0 or 1.0
        val isNewMoon = phase < 0.1f || phase > 0.9f
        assertEquals("Phase $phase should be near 0.0 or 1.0", true, isNewMoon)
    }
}
