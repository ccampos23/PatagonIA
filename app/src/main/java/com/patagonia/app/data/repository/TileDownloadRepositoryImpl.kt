package com.patagonia.app.data.repository

import android.content.Context
import com.patagonia.app.data.service.TileDownloadService
import com.patagonia.app.domain.model.DownloadedRegion
import com.patagonia.app.domain.model.TileDownloadState
import com.patagonia.app.domain.repository.TileDownloadRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TileDownloadRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TileDownloadRepository {

    // Simple in-memory storage of downloaded regions since service uses placeholders for now
    private val downloadedRegionsList = mutableListOf<DownloadedRegion>(
        DownloadedRegion(
            id = "preset-torres-del-paine",
            name = "Torres del Paine",
            sizeBytes = 75L * 1024L * 1024L,
            completedResources = 100,
            requiredResources = 100,
            isComplete = true
        )
    )

    override val downloadState: StateFlow<TileDownloadState>
        get() = TileDownloadService.downloadState

    override suspend fun startDownload(
        regionId: String,
        regionName: String,
        west: Double,
        south: Double,
        east: Double,
        north: Double,
        minZoom: Int,
        maxZoom: Int
    ) {
        // Start the foreground service
        val intent = TileDownloadService.createStartIntent(
            context = context,
            regionId = regionId,
            regionName = regionName,
            west = west,
            south = south,
            east = east,
            north = north,
            minZoom = minZoom,
            maxZoom = maxZoom
        )
        context.startService(intent)

        // Mock completion adding to the list (since service completes immediately)
        val estimatedSize = estimateDownloadSizeMB(west, south, east, north, minZoom, maxZoom).toLong()
        if (downloadedRegionsList.none { it.id == regionId }) {
            downloadedRegionsList.add(
                DownloadedRegion(
                    id = regionId,
                    name = regionName,
                    sizeBytes = estimatedSize * 1024L * 1024L,
                    completedResources = 100,
                    requiredResources = 100,
                    isComplete = true
                )
            )
        }
    }

    override fun pauseDownload(regionId: String) {
        context.startService(TileDownloadService.createPauseIntent(context))
    }

    override suspend fun resumeDownload(regionId: String) {
        context.startService(TileDownloadService.createResumeIntent(context, regionId))
    }

    override fun cancelDownload(regionId: String) {
        context.startService(TileDownloadService.createCancelIntent(context))
    }

    override suspend fun getDownloadedRegions(): List<DownloadedRegion> {
        return downloadedRegionsList.toList()
    }

    override suspend fun deleteRegion(regionId: String) {
        downloadedRegionsList.removeAll { it.id == regionId }
    }

    override fun estimateDownloadSizeMB(
        west: Double,
        south: Double,
        east: Double,
        north: Double,
        minZoom: Int,
        maxZoom: Int
    ): Int {
        val latSpan = Math.abs(north - south)
        val lngSpan = Math.abs(east - west)
        val area = latSpan * lngSpan
        
        // Simple heuristic for size estimation:
        // Capped zoom range factors in size
        val zoomFactor = (maxZoom - minZoom + 1).coerceAtLeast(1)
        val rawSize = (area * 50 * zoomFactor).toInt()
        
        return rawSize.coerceIn(15, 600)
    }
}
