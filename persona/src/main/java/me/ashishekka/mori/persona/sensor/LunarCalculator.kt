package me.ashishekka.mori.persona.sensor

import java.util.Calendar

/**
 * Pure logic utility for calculating the Moon's phase.
 * Uses a simplified algorithm (approx. 29.53 days per synodic month)
 * suitable for atmospheric rendering.
 */
class LunarCalculator {

    /**
     * Calculates the normalized moon phase (0.0 to 1.0).
     * 
     * @param calendar Current system time.
     * @return 0.0 = New Moon, 0.25 = First Quarter, 0.5 = Full Moon, 0.75 = Last Quarter.
     */
    fun calculateNormalizedPhase(calendar: Calendar): Float {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Simplified Conway's Moon Phase Algorithm
        var r = year % 100
        r %= 19
        if (r > 9) r -= 19
        r = ((r * 11) % 30) + month + day
        if (month < 3) r += 2
        r -= if (year < 2000) 4 else 8.3.toInt()
        
        r %= 30
        if (r < 0) r += 30

        // Map 0..30 days to 0.0..1.0
        return (r / 30f).coerceIn(0f, 1f)
    }
}
