package me.ashishekka.mori.engine.renderer

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas

/**
 * A renderer for the solid atmospheric background.
 * It is the primary contributor for the Foundation, Surface, and OnSurface colors.
 */
class StaticFallbackRenderer : EffectRenderer {

    override val zOrder: Int = Int.MIN_VALUE
    
    private var foundationColor: Int = 0
    private var accentColor: Int = 0
    private var surfaceColor: Int = 0
    private var onSurfaceColor: Int = 0

    // ZERO-ALLOCATION: Cache the returned palette object.
    private var cachedPalette: RendererPalette? = null

    override fun onSurfaceChanged(width: Int, height: Int, density: Float) {}

    override fun update(state: MoriEngineState) {
        val sun = state.chronosSunAltitude
        
        // Stash old values to check for changes
        val oldFoundation = foundationColor
        val oldAccent = accentColor
        val oldSurface = surfaceColor
        val oldOnSurface = onSurfaceColor
        
        // 1. Foundation Ramp (Background)
        foundationColor = when {
            sun < -0.5f -> 0xFF0D0221.toInt()
            sun < 0f -> lerpColor(0xFF0D0221.toInt(), 0xFF240B36.toInt(), (sun + 0.5f) * 2f)
            sun < 0.5f -> lerpColor(0xFF240B36.toInt(), 0xFF00B0FF.toInt(), sun * 2f)
            else -> 0xFF40C4FF.toInt()
        }

        // 2. Accent Ramp (Interactive Elements)
        accentColor = when {
            sun < 0f -> lerpColor(0xFFE040FB.toInt(), 0xFFFF5252.toInt(), (sun + 1f))
            else -> lerpColor(0xFFFF5252.toInt(), 0xFF00E676.toInt(), sun)
        }

        // 3. Derived Surface & OnSurface
        val isDark = sun <= 0.2f
        val surfaceAlpha = if (isDark) 0x4D000000 else 0x4DFFFFFF // 30% alpha
        surfaceColor = (surfaceAlpha.toLong() and 0xFF000000L).toInt() or (foundationColor and 0x00FFFFFF)
        onSurfaceColor = if (isDark) 0xFFFFFFFF.toInt() else 0xFF000000.toInt()

        // ROBUST CACHING: Invalidate if ANY color has changed.
        if (oldFoundation != foundationColor || oldAccent != accentColor || 
            oldSurface != surfaceColor || oldOnSurface != onSurfaceColor) {
            cachedPalette = null
        }
    }

    override fun getPaletteContribution(): RendererPalette? {
        if (cachedPalette == null) {
            cachedPalette = RendererPalette(
                accent = accentColor,
                foundation = foundationColor,
                surface = surfaceColor,
                onSurface = onSurfaceColor
            )
        }
        return cachedPalette
    }

    override fun render(canvas: EngineCanvas) {
        canvas.drawColor(foundationColor)
    }

    private fun lerpColor(from: Int, to: Int, fraction: Float): Int {
        val f = fraction.coerceIn(0f, 1f)
        val a1 = (from shr 24) and 0xff; val r1 = (from shr 16) and 0xff; val g1 = (from shr 8) and 0xff; val b1 = from and 0xff
        val a2 = (to shr 24) and 0xff; val r2 = (to shr 16) and 0xff; val g2 = (to shr 8) and 0xff; val b2 = to and 0xff
        return ((a1 + (a2 - a1) * f).toInt() shl 24) or
               ((r1 + (r2 - r1) * f).toInt() shl 16) or
               ((g1 + (g2 - g1) * f).toInt() shl 8) or
               ((b1 + (b2 - b1) * f).toInt())
    }
}
