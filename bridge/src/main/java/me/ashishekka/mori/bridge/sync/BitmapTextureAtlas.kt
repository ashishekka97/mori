package me.ashishekka.mori.bridge.sync

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import me.ashishekka.mori.engine.core.models.AtlasRegion

/**
 * A simple shelf-packer for packing multiple bitmaps into a single large atlas.
 * This helps reduce texture switches and improve rendering performance.
 *
 * @param width The width of the atlas in pixels.
 * @param height The height of the atlas in pixels.
 * @param padding The padding between assets in pixels to prevent texture bleeding.
 */
class BitmapTextureAtlas(
    val width: Int = 2048,
    val height: Int = 2048,
    private val padding: Int = 2
) {
    private val atlas: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    private val canvas: Canvas = Canvas(atlas)
    private val packer = TextureAtlasPacker(width, height, padding)

    /**
     * Packs a new bitmap into the atlas and returns its position as an AtlasRegion.
     * Returns null if there is no space left in the atlas.
     */
    fun pack(bitmap: Bitmap): AtlasRegion? {
        val region = packer.pack(bitmap.width, bitmap.height) ?: return null
        
        // Draw the bitmap into the atlas at the location determined by the packer
        canvas.drawBitmap(bitmap, region.left.toFloat(), region.top.toFloat(), null)

        return region
    }

    /**
     * Returns the backing bitmap of the atlas.
     */
    fun getAtlasBitmap(): Bitmap = atlas
    
    /**
     * Clears the atlas by filling it with transparency and resetting the cursor.
     */
    fun clear() {
        atlas.eraseColor(Color.TRANSPARENT)
        packer.clear()
    }
}
