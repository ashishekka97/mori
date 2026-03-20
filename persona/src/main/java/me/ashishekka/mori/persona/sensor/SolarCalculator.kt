package me.ashishekka.mori.persona.sensor

import java.util.Calendar
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin

/**
 * Pure logic utility for calculating celestial positions.
 * Uses simplified astronomical formulas to provide "good enough" accuracy for 
 * atmospheric rendering while maintaining high performance.
 */
class SolarCalculator {

    /**
     * Calculates the normalized altitude of the sun (-1.0 to 1.0).
     * 
     * @param calendar Current system time.
     * @param latitude Device latitude in degrees.
     * @return Altitude where 1.0 is Zenith (Noon), 0.0 is Horizon, and -1.0 is Nadir (Midnight).
     */
    fun calculateNormalizedAltitude(calendar: Calendar, latitude: Double): Float {
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // 1. Calculate Solar Declination (Delta)
        // Earth's tilt relative to the sun (approx -23.45 to 23.45 degrees)
        val declination = 23.45 * sin(Math.toRadians(360.0 / 365.0 * (dayOfYear - 81)))

        // 2. Calculate Hour Angle (H)
        // Degrees from solar noon (15 degrees per hour)
        val fractionalHour = hour + (minute / 60.0)
        val hourAngle = 15.0 * (fractionalHour - 12.0)

        // 3. Calculate Elevation (Altitude)
        // sin(Alt) = sin(Lat) * sin(Delta) + cos(Lat) * cos(Delta) * cos(H)
        val latRad = Math.toRadians(latitude)
        val declRad = Math.toRadians(declination)
        val hourRad = Math.toRadians(hourAngle)

        val sinAlt = sin(latRad) * sin(declRad) + cos(latRad) * cos(declRad) * cos(hourRad)
        val altitudeRad = asin(sinAlt)
        val altitudeDeg = Math.toDegrees(altitudeRad)

        // 4. Normalize to -1.0 -> 1.0 range
        // Max possible elevation depends on latitude/season, but for rendering we 
        // can map -90..90 degrees to -1.0..1.0.
        return (altitudeDeg / 90.0).toFloat().coerceIn(-1f, 1f)
    }
}
