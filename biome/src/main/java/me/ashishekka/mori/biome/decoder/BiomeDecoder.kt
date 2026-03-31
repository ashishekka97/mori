package me.ashishekka.mori.biome.decoder

import kotlinx.serialization.json.Json
import me.ashishekka.mori.biome.compiler.ExpressionCompiler
import me.ashishekka.mori.biome.models.BiomeModel
import me.ashishekka.mori.engine.core.MoriWallpaper
import me.ashishekka.mori.engine.core.models.LayerType
import me.ashishekka.mori.engine.core.models.MoriLayer
import me.ashishekka.mori.engine.core.models.RenderProperty

/**
 * Decodes JSON Biome definitions into executable [MoriWallpaper] objects.
 * This is the primary bridge between the declarative UI and the high-performance Engine.
 * 
 * DESIGN PRINCIPLE:
 * Partial Success. If a specific layer or expression is malformed, the decoder 
 * will skip it or fall back to defaults rather than discarding the entire biome.
 */
object BiomeDecoder {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    /**
     * Decodes a JSON string into a [BiomeModel].
     * Returns null if the JSON is fundamentally broken.
     */
    fun decode(jsonString: String?): BiomeModel? {
        if (jsonString.isNullOrBlank()) return null
        
        return try {
            json.decodeFromString<BiomeModel>(jsonString)
        } catch (e: Exception) {
            // Root JSON error (e.g., missing mandatory fields, syntax error)
            null
        }
    }

    /**
     * Converts a [BiomeModel] into a list of [MoriLayer] primitives ready for the Engine.
     */
    fun compileToLayers(model: BiomeModel?): List<MoriLayer> {
        if (model == null) return emptyList()
        
        return model.layers.mapNotNull { layerModel ->
            try {
                val engineLayer = MoriLayer(
                    id = layerModel.id,
                    type = LayerType.fromString(layerModel.type),
                    zOrder = layerModel.zOrder
                )
                
                layerModel.expressions.forEach { (propertyName, expression) ->
                    val propertyIndex = mapPropertyNameToIndex(propertyName)
                    if (propertyIndex != -1) {
                        // ExpressionCompiler is fail-safe and returns empty array on error
                        engineLayer.propertyRules[propertyIndex] = ExpressionCompiler.compile(expression)
                    }
                }
                
                engineLayer
            } catch (e: Exception) {
                // Skip specific malformed layer
                null
            }
        }
    }

    private fun mapPropertyNameToIndex(name: String): Int {
        return when (name.lowercase()) {
            "x" -> RenderProperty.INDEX_X
            "y" -> RenderProperty.INDEX_Y
            "scale_x" -> RenderProperty.INDEX_SCALE_X
            "scale_y" -> RenderProperty.INDEX_SCALE_Y
            "rotation" -> RenderProperty.INDEX_ROTATION
            "width" -> RenderProperty.INDEX_WIDTH
            "height" -> RenderProperty.INDEX_HEIGHT
            "stroke_width" -> RenderProperty.INDEX_STROKE_WIDTH
            "alpha" -> RenderProperty.INDEX_ALPHA
            "color_primary" -> RenderProperty.INDEX_COLOR_PRIMARY
            "color_secondary" -> RenderProperty.INDEX_COLOR_SECONDARY
            "custom_a" -> RenderProperty.INDEX_CUSTOM_A
            "custom_b" -> RenderProperty.INDEX_CUSTOM_B
            "custom_c" -> RenderProperty.INDEX_CUSTOM_C
            "custom_d" -> RenderProperty.INDEX_CUSTOM_D
            "custom_e" -> RenderProperty.INDEX_CUSTOM_E
            else -> -1
        }
    }
}
