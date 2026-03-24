package me.ashishekka.mori.ui.theme.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Perceptual color utilities for the Pulse Design System.
 * 
 * PARITY TWIN: This file has a twin in the :engine module to ensure visual parity
 * between the Engine's synthesis and the Design Lab's fallback logic.
 */
object ColorUtils {

    /**
     * Interpolates between two colors in OKLab space.
     * Used by rememberPulseColors to ensure the Design Lab is vibrant.
     */
    fun lerpColorOklab(from: Color, to: Color, fraction: Float): Color {
        val argb1 = from.toArgb()
        val argb2 = to.toArgb()
        
        val l1 = rgbToOklabL(argb1)
        val a1 = rgbToOklabA(argb1)
        val b1 = rgbToOklabB(argb1)
        
        val l2 = rgbToOklabL(argb2)
        val a2 = rgbToOklabA(argb2)
        val b2 = rgbToOklabB(argb2)
        
        val l = l1 + (l2 - l1) * fraction
        val a = a1 + (a2 - a1) * fraction
        val b = b1 + (b2 - b1) * fraction
        
        return Color(oklabToRgb(l, a, b))
    }

    private fun rgbToOklabL(color: Int): Float {
        val r = ((color shr 16) and 0xFF) / 255f
        val g = ((color shr 8) and 0xFF) / 255f
        val b = (color and 0xFF) / 255f
        val l = 0.4122214708f * r + 0.5363325363f * g + 0.0514459929f * b
        val m = 0.2119034982f * r + 0.6806995451f * g + 0.1073969566f * b
        val s = 0.0883024619f * r + 0.2817188376f * g + 0.6299787005f * b
        val l_ = Math.cbrt(l.toDouble()).toFloat()
        val m_ = Math.cbrt(m.toDouble()).toFloat()
        val s_ = Math.cbrt(s.toDouble()).toFloat()
        return 0.2104542553f * l_ + 0.7936177850f * m_ - 0.0040720468f * s_
    }

    private fun rgbToOklabA(color: Int): Float {
        val r = ((color shr 16) and 0xFF) / 255f
        val g = ((color shr 8) and 0xFF) / 255f
        val b = (color and 0xFF) / 255f
        val l = 0.4122214708f * r + 0.5363325363f * g + 0.0514459929f * b
        val m = 0.2119034982f * r + 0.6806995451f * g + 0.1073969566f * b
        val s = 0.0883024619f * r + 0.2817188376f * g + 0.6299787005f * b
        val l_ = Math.cbrt(l.toDouble()).toFloat()
        val m_ = Math.cbrt(m.toDouble()).toFloat()
        val s_ = Math.cbrt(s.toDouble()).toFloat()
        return 1.9779984951f * l_ - 2.4285922050f * m_ + 0.4505937099f * s_
    }

    private fun rgbToOklabB(color: Int): Float {
        val r = ((color shr 16) and 0xFF) / 255f
        val g = ((color shr 8) and 0xFF) / 255f
        val b = (color and 0xFF) / 255f
        val l = 0.4122214708f * r + 0.5363325363f * g + 0.0514459929f * b
        val m = 0.2119034982f * r + 0.6806995451f * g + 0.1073969566f * b
        val s = 0.0883024619f * r + 0.2817188376f * g + 0.6299787005f * b
        val l_ = Math.cbrt(l.toDouble()).toFloat()
        val m_ = Math.cbrt(m.toDouble()).toFloat()
        val s_ = Math.cbrt(s.toDouble()).toFloat()
        return 0.0259040371f * l_ + 0.7827717662f * m_ - 0.8086757660f * s_
    }

    private fun oklabToRgb(l: Float, a: Float, b: Float): Int {
        val l_ = l + 0.3963377774f * a + 0.2158037573f * b
        val m_ = l - 0.1055613458f * a - 0.0638541728f * b
        val s_ = l - 0.0894841775f * a - 1.2914855480f * b
        val l_3 = l_ * l_ * l_
        val m_3 = m_ * m_ * m_
        val s_3 = s_ * s_ * s_
        val r = 4.0767416621f * l_3 - 3.3077115913f * m_3 + 0.2309699292f * s_3
        val g = -1.2684380046f * l_3 + 2.6097574011f * m_3 - 0.3413193965f * s_3
        val b_ = -0.0041960863f * l_3 - 0.7034186147f * m_3 + 1.7076147010f * s_3
        val rInt = (r.coerceIn(0f, 1f) * 255).toInt()
        val gInt = (g.coerceIn(0f, 1f) * 255).toInt()
        val bInt = (b_.coerceIn(0f, 1f) * 255).toInt()
        return 0xFF000000.toInt() or (rInt shl 16) or (gInt shl 8) or bInt
    }
}
