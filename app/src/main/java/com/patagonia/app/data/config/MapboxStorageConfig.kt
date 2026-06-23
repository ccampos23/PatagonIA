package com.patagonia.app.data.config

import java.io.File

/**
 * Mapbox storage and caching configuration helper (D-24, D-25).
 * Centralizes TileStore path and cache quota calculations.
 */
object MapboxStorageConfig {

    /** Subdirectory name for map tile storage within internal storage */
    const val TILE_STORE_DIR = "mapbox_tiles"

    /** Ambient cache size limit in bytes: 250MB (D-25) */
    const val AMBIENT_CACHE_SIZE_BYTES: Long = 250L * 1024L * 1024L

    /**
     * Resolve the TileStore storage path within internal storage (D-24).
     * Uses context.filesDir to ensure data stays in app-private internal storage,
     * protecting map data from external access (T-03-03).
     *
     * @param filesDir The application's internal files directory (context.filesDir)
     * @return Absolute path for TileStore initialization
     */
    fun resolveTileStorePath(filesDir: File): String {
        return File(filesDir, TILE_STORE_DIR).absolutePath
    }

    /**
     * Get the ambient cache size limit in megabytes for display purposes.
     */
    fun getAmbientCacheSizeMb(): Long = AMBIENT_CACHE_SIZE_BYTES / (1024L * 1024L)
}
