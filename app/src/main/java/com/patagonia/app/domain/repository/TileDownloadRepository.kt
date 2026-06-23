package com.patagonia.app.domain.repository

import com.patagonia.app.domain.model.DownloadedRegion
import com.patagonia.app.domain.model.TileDownloadState
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for managing offline tile downloads.
 * Implementation lives in the data layer with Mapbox SDK integration.
 */
interface TileDownloadRepository {

    /**
     * Observable download state for the UI.
     */
    val downloadState: StateFlow<TileDownloadState>

    /**
     * Start downloading a tile region with the given bounding box.
     *
     * @param regionId Unique identifier for this region
     * @param regionName Human-readable name
     * @param west Western longitude bound
     * @param south Southern latitude bound
     * @param east Eastern longitude bound
     * @param north Northern latitude bound
     * @param minZoom Minimum zoom level to download
     * @param maxZoom Maximum zoom level to download (capped at 15 per D-18)
     */
    suspend fun startDownload(
        regionId: String,
        regionName: String,
        west: Double,
        south: Double,
        east: Double,
        north: Double,
        minZoom: Int = 0,
        maxZoom: Int = 14
    )

    /**
     * Pause an active download.
     */
    fun pauseDownload(regionId: String)

    /**
     * Resume a paused download. Calls loadTileRegion again with the same ID (auto-resumes).
     */
    suspend fun resumeDownload(regionId: String)

    /**
     * Cancel and remove a download.
     */
    fun cancelDownload(regionId: String)

    /**
     * List all downloaded tile regions.
     */
    suspend fun getDownloadedRegions(): List<DownloadedRegion>

    /**
     * Delete a downloaded region from the tile store.
     */
    suspend fun deleteRegion(regionId: String)

    /**
     * Estimate the download size in MB for a given bounding box and zoom range.
     * Returns -1 if estimation is not possible.
     */
    fun estimateDownloadSizeMB(
        west: Double,
        south: Double,
        east: Double,
        north: Double,
        minZoom: Int,
        maxZoom: Int
    ): Int
}
