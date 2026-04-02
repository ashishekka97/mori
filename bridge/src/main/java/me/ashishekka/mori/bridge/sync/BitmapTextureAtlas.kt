package me.ashishekka.mori.bridge.sync

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect

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
    
    private var currentX = 0
    private var currentY = 0
    private var currentRowHeight = 0

    /**
     * Packs a new bitmap into the atlas and returns its position.
     * Returns null if there is no space left in the atlas.
     */
    fun pack(bitmap: Bitmap): Rect? {
        // Check if we need to move to the next row
        if (currentX + bitmap.width + padding > width) {
            currentX = 0
            currentY += currentRowHeight + padding
            currentRowHeight = 0
        }

        // Check if we have vertical space left
        if (currentY + bitmap.height + padding > height) {
            android.util.Log.e("Mori", "BitmapTextureAtlas is FULL! Cannot pack bitmap ${bitmap.width}x${bitmap.height}")
            return null
        }

        // Define the destination rectangle in the atlas
        val rect = Rect(currentX, currentY, currentX + bitmap.width, currentY + bitmap.height)
        
        // Draw the bitmap into the atlas
        canvas.drawBitmap(bitmap, currentX.toFloat(), currentY.toFloat(), null)

        // Update packing state
        currentX += bitmap.width + padding
        currentRowHeight = maxOf(currentRowHeight, bitmap.height)

        return rect
    }

    /**
     * Returns the backing bitmap of the atlas.
     */
    fun getAtlasBitmap(): Bitmap = atlas
    
    /**
     * Clears the atlas by filling it with transparency and resetting the cursor.
     */
    fun clear() {
        atlas.eraseColor(android.graphics.Color.TRANSPARENT)
        currentX = 0
        currentY = 0
        currentRowHeight = 0
    }
}
