package me.ashishekka.mori.bridge.sync

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class TextureAtlasPackerTest {

    @Test
    fun `pack should return correct rect and advance cursor`() {
        val packer = TextureAtlasPacker(width = 100, height = 100, padding = 2)
        
        val rect1 = packer.pack(10, 10)
        
        assertNotNull(rect1)
        assertEquals(0, rect1!!.left)
        assertEquals(0, rect1.top)
        assertEquals(10, rect1.width)
        assertEquals(10, rect1.height)
    }

    @Test
    fun `pack should move to next row when out of horizontal space`() {
        val packer = TextureAtlasPacker(width = 25, height = 100, padding = 2)
        
        packer.pack(10, 10)
        val rect2 = packer.pack(15, 10) // 10 + 2 (padding) + 15 = 27 > 25

        assertNotNull(rect2)
        assertEquals(0, rect2!!.left)
        assertEquals(12, rect2.top) // 10 (height) + 2 (padding)
    }

    @Test
    fun `pack should return null when out of vertical space`() {
        val packer = TextureAtlasPacker(width = 10, height = 15, padding = 2)
        
        packer.pack(10, 10)
        val rect2 = packer.pack(10, 10) // Forces new row: 10 + 2 (padding) + 10 = 22 > 15

        assertNull(rect2)
    }
    
    @Test
    fun `clear should reset packer state`() {
        val packer = TextureAtlasPacker(width = 100, height = 100, padding = 2)
        
        packer.pack(10, 10)
        packer.clear()
        
        val rect1 = packer.pack(10, 10)
        assertNotNull(rect1)
        assertEquals(0, rect1!!.left)
        assertEquals(0, rect1.top)
    }
}
