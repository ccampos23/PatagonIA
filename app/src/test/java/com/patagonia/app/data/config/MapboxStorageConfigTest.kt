package com.patagonia.app.data.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Unit tests for [MapboxStorageConfig].
 * Verifies storage path resolution (D-24) and cache quota (D-25).
 */
class MapboxStorageConfigTest {

    @Test
    fun `resolveTileStorePath returns path inside filesDir`() {
        val filesDir = File("/data/data/com.patagonia.app/files")
        val path = MapboxStorageConfig.resolveTileStorePath(filesDir)

        assertTrue(path.startsWith(filesDir.absolutePath))
        assertTrue(path.endsWith(MapboxStorageConfig.TILE_STORE_DIR))
    }

    @Test
    fun `resolveTileStorePath uses mapbox_tiles subdirectory`() {
        val filesDir = File("/data/data/com.patagonia.app/files")
        val path = MapboxStorageConfig.resolveTileStorePath(filesDir)
        val expected = File(filesDir, "mapbox_tiles").absolutePath

        assertEquals(expected, path)
    }

    @Test
    fun `ambient cache size is 250MB in bytes`() {
        val expectedBytes = 250L * 1024L * 1024L
        assertEquals(expectedBytes, MapboxStorageConfig.AMBIENT_CACHE_SIZE_BYTES)
    }

    @Test
    fun `getAmbientCacheSizeMb returns 250`() {
        assertEquals(250L, MapboxStorageConfig.getAmbientCacheSizeMb())
    }

    @Test
    fun `TILE_STORE_DIR constant is correct`() {
        assertEquals("mapbox_tiles", MapboxStorageConfig.TILE_STORE_DIR)
    }
}
