package me.ashishekka.mori.bridge.metrics

import me.ashishekka.mori.engine.core.models.ScaleMode
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MetricCalculatorTest {

    private lateinit var calculator: MetricCalculator

    @Before
    fun setUp() {
        calculator = MetricCalculator()
    }

    @Test
    fun `dpToPx should calculate correctly based on density`() {
        // Given 2.0x density (HDPI)
        calculator.updateMetrics(1080, 1920, 2.0f)
        
        // Then
        assertEquals(200f, calculator.dpToPx(100f), 0.01f)
    }

    @Test
    fun `calculateScaleFactor FIT should fit within boundaries`() {
        // Given a tall screen (1080x2400)
        calculator.updateMetrics(1080, 2400, 1.0f)
        
        // Artist designed for 1000x1000
        val scale = calculator.calculateScaleFactor(1000f, 1000f, ScaleMode.FIT)
        
        // Should scale to 1.08 to fit the width, leaving height empty
        assertEquals(1.08f, scale, 0.01f)
    }

    @Test
    fun `calculateScaleFactor FILL should cover boundaries`() {
        // Given a tall screen (1080x2400)
        calculator.updateMetrics(1080, 2400, 1.0f)
        
        // Artist designed for 1000x1000
        val scale = calculator.calculateScaleFactor(1000f, 1000f, ScaleMode.FILL)
        
        // Should scale to 2.4 to cover the entire height, cropping width
        assertEquals(2.4f, scale, 0.01f)
    }

    @Test
    fun `getCenterOffsets should center content correctly`() {
        // Given
        calculator.updateMetrics(1000, 2000, 1.0f)
        val refW = 500f
        val refH = 500f
        val scale = 2.0f // Scales to 1000x1000
        
        // When
        val offsetX = calculator.getCenterXOffset(refW, scale)
        val offsetY = calculator.getCenterYOffset(refH, scale)
        
        // Then
        assertEquals(0f, offsetX, 0.01f) // Matches width exactly
        assertEquals(500f, offsetY, 0.01f) // (2000 - 1000) / 2
    }

    @Test
    fun `orientation should update correctly`() {
        calculator.updateMetrics(1080, 1920, 1.0f)
        assertEquals(Orientation.PORTRAIT, calculator.orientation)

        calculator.updateMetrics(1920, 1080, 1.0f)
        assertEquals(Orientation.LANDSCAPE, calculator.orientation)
    }
}
