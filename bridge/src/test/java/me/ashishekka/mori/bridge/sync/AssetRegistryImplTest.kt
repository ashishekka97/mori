package me.ashishekka.mori.bridge.sync

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.mockk.*
import me.ashishekka.mori.engine.core.models.AssetType
import me.ashishekka.mori.engine.core.models.AtlasRegion
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.InputStream

class AssetRegistryImplTest {

    private lateinit var registry: AssetRegistryImpl

    @Before
    fun setUp() {
        mockkStatic(Bitmap::class)
        mockkStatic(BitmapFactory::class)
        every { Bitmap.createBitmap(any<Int>(), any<Int>(), any<Bitmap.Config>()) } returns mockk(relaxed = true)
        registry = AssetRegistryImpl()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `registerAsset should store coordinates in AtlasRegion via packer`() {
        // Given
        val resId = 1
        val mockStream = mockk<InputStream>(relaxed = true)
        val mockBitmap = mockk<Bitmap>(relaxed = true) {
            every { width } returns 10
            every { height } returns 10
        }
        every { BitmapFactory.decodeStream(any()) } returns mockBitmap
        
        // Mock the atlas to return a specific region
        val expectedRegion = AtlasRegion(5, 5, 10, 10)
        mockkConstructor(BitmapTextureAtlas::class)
        every { anyConstructed<BitmapTextureAtlas>().pack(any()) } returns expectedRegion

        // When
        registry.registerAsset(resId, AssetType.BITMAP, mockStream)

        // Then
        val actualRegion = registry.getAtlasRegion(resId)
        assertEquals(expectedRegion, actualRegion)
        assert(registry.isReady(resId))
    }

    @Test
    fun `isReady should return false for unregistered assets`() {
        assert(!registry.isReady(999))
    }

    @Test
    fun `clear should reset loaded assets`() {
        registry.clear()
        assert(!registry.isReady(1))
    }
}
