package me.ashishekka.mori.bridge.sync

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.mockk.*
import me.ashishekka.mori.engine.core.models.AssetType
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
        every { Bitmap.createBitmap(any<Int>(), any<Int>(), any<Bitmap.Config>()) } returns mockk(relaxed = true)
        registry = AssetRegistryImpl()
    }

    @After
    fun tearDown() {
        unmockkAll()
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
