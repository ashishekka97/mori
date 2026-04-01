package me.ashishekka.mori.engine.core.models

/**
 * Static mapping of visual property indices for the Mori Engine.
 * This is the "VRAM" model where the Engine reads results from a flat memory buffer.
 * 
 * DESIGN PRINCIPLE:
 * The Engine is "dumb." It doesn't know what these properties mean to the Biome
 * (e.g., Hunger vs. Pollution). It only knows how to use them for rendering.
 */
object RenderProperty {
    // --- Standard Geometric Properties (0-4) ---
    const val INDEX_X = 0
    const val INDEX_Y = 1
    const val INDEX_SCALE_X = 2
    const val INDEX_SCALE_Y = 3
    const val INDEX_ROTATION = 4
    const val INDEX_WIDTH = 5
    const val INDEX_HEIGHT = 6
    const val INDEX_STROKE_WIDTH = 7
    
    // --- Standard Visual Properties (8-10) ---
    const val INDEX_ALPHA = 8
    const val INDEX_COLOR_PRIMARY = 9
    const val INDEX_COLOR_SECONDARY = 10
    
    // --- Semantic Expansion Slots (11-15) ---
    /** Can be used for custom shader uniforms, animation frames, or biome-specific logic. */
    const val INDEX_CUSTOM_A = 11
    const val INDEX_CUSTOM_B = 12
    const val INDEX_CUSTOM_C = 13
    const val INDEX_CUSTOM_D = 14
    const val INDEX_CUSTOM_E = 15
    
    const val BUFFER_SIZE = 16
}
