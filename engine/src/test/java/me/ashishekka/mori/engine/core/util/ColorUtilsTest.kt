package me.ashishekka.mori.engine.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorUtilsTest {

    @Test
    fun `lerpColorOklab should return exact start color at fraction 0`() {
        val start = 0xFFFF0000.toInt() // Red
        val end = 0xFF0000FF.toInt()   // Blue
        
        val result = ColorUtils.lerpColorOklab(start, end, 0.0f)
        assertEquals(start, result)
    }

    @Test
    fun `lerpColorOklab should return exact end color at fraction 1`() {
        val start = 0xFFFF0000.toInt() // Red
        val end = 0xFF0000FF.toInt()   // Blue
        
        val result = ColorUtils.lerpColorOklab(start, end, 1.0f)
        assertEquals(end, result)
    }

    @Test
    fun `lerpColorOklab should avoid the Grey Trap of linear RGB`() {
        // Red and Cyan are opposites. Linear RGB lerp at 0.5 results in (127, 127, 127) - flat grey.
        // OKLab lerp at 0.5 should result in a color with much higher perceptual vibrancy.
        val red = 0xFFFF0000.toInt()
        val cyan = 0xFF00FFFF.toInt()
        
        val result = ColorUtils.lerpColorOklab(red, cyan, 0.5f)
        
        // Calculate saturation (simplified)
        val r = (result shr 16) and 0xFF
        val g = (result shr 8) and 0xFF
        val b = result and 0xFF
        
        val max = maxOf(r, maxOf(g, b))
        val min = minOf(r, minOf(g, b))
        val saturation = if (max == 0) 0 else (max - min)
        
        // In linear RGB, saturation at the midpoint of Red/Cyan is nearly 0.
        // In OKLab, we expect significant saturation to remain (> 50).
        assertTrue("OKLab should maintain vibrancy (Saturation: $saturation)", saturation > 50)
    }

    @Test
    fun `lerpColorOklab should handle extreme values without crashing`() {
        val black = 0xFF000000.toInt()
        val white = 0xFFFFFFFF.toInt()
        
        // Ensure no NaNs or Infinities are produced during conversion
        val result = ColorUtils.lerpColorOklab(black, white, 0.5f)
        assertTrue(result != 0)
    }
}
