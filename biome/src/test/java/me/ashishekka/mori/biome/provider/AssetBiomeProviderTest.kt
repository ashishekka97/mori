package me.ashishekka.mori.biome.provider

import android.content.Context
import android.content.res.AssetManager
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.ByteArrayInputStream

class AssetBiomeProviderTest {

    private val mockContext = mockk<Context>()
    private val mockAssets = mockk<AssetManager>()
    private val provider = AssetBiomeProvider(mockContext)

    @Test
    fun `getBiome should load and decode valid JSON from assets`() {
        // Given
        val biomeId = "test_prism"
        val json = """
            {
                "id": "test_prism",
                "name": "Test Prism"
            }
        """.trimIndent()
        
        every { mockContext.assets } returns mockAssets
        every { mockAssets.open("biomes/$biomeId.json") } returns ByteArrayInputStream(json.toByteArray())

        // When
        val result = provider.getBiome(biomeId)

        // Then
        assertEquals("test_prism", result?.id)
        assertEquals("Test Prism", result?.name)
    }

    @Test
    fun `getBiome should return null if file is missing`() {
        // Given
        val biomeId = "missing_biome"
        every { mockContext.assets } returns mockAssets
        every { mockAssets.open(any()) } throws java.io.FileNotFoundException()

        // When
        val result = provider.getBiome(biomeId)

        // Then
        assertNull(result)
    }

    @Test
    fun `getBiome should return null if JSON is malformed`() {
        // Given
        val biomeId = "broken_json"
        val brokenJson = "{ \"id\": \"test\" " // Missing closing bracket
        
        every { mockContext.assets } returns mockAssets
        every { mockAssets.open("biomes/$biomeId.json") } returns ByteArrayInputStream(brokenJson.toByteArray())

        // When
        val result = provider.getBiome(biomeId)

        // Then
        assertNull(result)
    }
}
