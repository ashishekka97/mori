package me.ashishekka.mori.engine.core.models

/**
 * Standard uniform names for mapping [RenderProperty] values to AGSL Shaders.
 */
object ShaderUniforms {
    const val X = "u_x"
    const val Y = "u_y"
    const val SCALE_X = "u_scale_x"
    const val SCALE_Y = "u_scale_y"
    const val ROTATION = "u_rotation"
    const val WIDTH = "u_width"
    const val HEIGHT = "u_height"
    const val STROKE_WIDTH = "u_stroke_width"

    const val ALPHA = "u_alpha"
    const val COLOR_PRIMARY = "u_color_primary"
    const val COLOR_SECONDARY = "u_color_secondary"

    const val CUSTOM_A = "u_custom_a"
    const val CUSTOM_B = "u_custom_b"
    const val CUSTOM_C = "u_custom_c"
    const val CUSTOM_D = "u_custom_d"
    const val CUSTOM_E = "u_custom_e"

    val UNIFORM_NAMES = arrayOf(
        X, Y, SCALE_X, SCALE_Y, ROTATION, WIDTH, HEIGHT, STROKE_WIDTH,
        ALPHA, COLOR_PRIMARY, COLOR_SECONDARY,
        CUSTOM_A, CUSTOM_B, CUSTOM_C, CUSTOM_D, CUSTOM_E
    )
}