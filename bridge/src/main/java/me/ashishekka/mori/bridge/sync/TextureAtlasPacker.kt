package me.ashishekka.mori.bridge.sync

/**
 * Platform-agnostic shelf-packing logic.
 * Decoupled from Android's Bitmap/Rect for easy unit testing.
 */
class TextureAtlasPacker(
    val width: Int,
    val height: Int,
    private val padding: Int = 2
) {
    private var currentX = 0
    private var currentY = 0
    private var currentRowHeight = 0

    data class PackedRect(val left: Int, val top: Int, val width: Int, val height: Int)

    /**
     * Finds a spot for a rectangle of given [itemWidth] and [itemHeight].
     * Returns the coordinates of the packed rectangle, or null if it doesn't fit.
     */
    fun pack(itemWidth: Int, itemHeight: Int): PackedRect? {
        // Check if we need to move to the next row
        if (currentX + itemWidth + padding > width) {
            currentX = 0
            currentY += currentRowHeight + padding
            currentRowHeight = 0
        }

        // Check if we have vertical space left
        if (currentY + itemHeight + padding > height) {
            return null
        }

        val rect = PackedRect(currentX, currentY, itemWidth, itemHeight)

        // Update packing state
        currentX += itemWidth + padding
        currentRowHeight = maxOf(currentRowHeight, itemHeight)

        return rect
    }

    fun clear() {
        currentX = 0
        currentY = 0
        currentRowHeight = 0
    }
}
