package com.patagonia.app.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.patagonia.app.domain.model.TileDownloadState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Foreground Service for downloading offline map tile regions (D-16).
 *
 * Uses foregroundServiceType="dataSync" for background download reliability.
 * Implements Android 15 onTimeout for the 6-hour dataSync limit (D-17).
 *
 * NOTE: Mapbox TileStore calls are commented/placeholder until the SDK
 * dependency is uncommented (requires MAPBOX_DOWNLOADS_TOKEN).
 */
class TileDownloadService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var downloadJob: Job? = null

    /**
     * Notification throttling: update at most once per second.
     */
    private var lastNotificationUpdateMs = 0L

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_DOWNLOAD -> {
                val regionId = intent.getStringExtra(EXTRA_REGION_ID) ?: return START_NOT_STICKY
                val regionName = intent.getStringExtra(EXTRA_REGION_NAME) ?: regionId
                val west = intent.getDoubleExtra(EXTRA_WEST, 0.0)
                val south = intent.getDoubleExtra(EXTRA_SOUTH, 0.0)
                val east = intent.getDoubleExtra(EXTRA_EAST, 0.0)
                val north = intent.getDoubleExtra(EXTRA_NORTH, 0.0)
                val minZoom = intent.getIntExtra(EXTRA_MIN_ZOOM, 0)
                val maxZoom = intent.getIntExtra(EXTRA_MAX_ZOOM, 14)

                startForegroundWithNotification(regionName)
                startDownload(regionId, regionName, west, south, east, north, minZoom, maxZoom)
            }
            ACTION_PAUSE_DOWNLOAD -> {
                pauseDownload()
            }
            ACTION_RESUME_DOWNLOAD -> {
                val regionId = intent.getStringExtra(EXTRA_REGION_ID) ?: return START_NOT_STICKY
                resumeDownload(regionId)
            }
            ACTION_CANCEL_DOWNLOAD -> {
                cancelDownload()
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * Android 15 (SDK 35) timeout handler for dataSync foreground services.
     * The 6-hour limit has been reached — MUST stop within seconds (D-17).
     */
    override fun onTimeout(startId: Int) {
        val currentState = _downloadState.value
        if (currentState is TileDownloadState.Downloading) {
            _downloadState.value = TileDownloadState.TimedOut(
                regionId = currentState.regionId,
                regionName = currentState.regionName,
                completedResources = currentState.completedResources,
                requiredResources = currentState.requiredResources
            )
        }

        // Cancel the download job — checkpoint is in the TimedOut state
        downloadJob?.cancel()

        // TODO: When Mapbox SDK is available, call cancelable?.cancel() to save TileStore checkpoint

        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf(startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    // --- Notification Channel ---

    internal fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Offline map tile download progress"
            setShowBadge(false)
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun startForegroundWithNotification(regionName: String) {
        val notification = buildNotification(regionName, 0)
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            } else {
                0
            }
        )
    }

    private fun buildNotification(regionName: String, progress: Int): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Downloading $regionName")
            .setContentText("$progress% complete")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progress, progress == 0)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun updateNotification(regionName: String, progress: Int) {
        val now = System.currentTimeMillis()
        if (now - lastNotificationUpdateMs < NOTIFICATION_THROTTLE_MS) return
        lastNotificationUpdateMs = now

        val notification = buildNotification(regionName, progress)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    // --- Download Operations ---

    private fun startDownload(
        regionId: String,
        regionName: String,
        west: Double,
        south: Double,
        east: Double,
        north: Double,
        minZoom: Int,
        maxZoom: Int
    ) {
        _downloadState.value = TileDownloadState.Downloading(
            regionId = regionId,
            regionName = regionName,
            completedResources = 0,
            requiredResources = 0,
            completedBytes = 0,
            erroredResources = 0
        )

        downloadJob = serviceScope.launch {
            // TODO: When Mapbox SDK is available, implement:
            // 1. Create TilesetDescriptor with styleURI, minZoom, maxZoom
            // 2. Create bounding box Polygon from (west, south, east, north)
            // 3. Create TileRegionLoadOptions with geometry and descriptors
            // 4. Call tileStore.loadTileRegion() with progress callback
            //
            // The progress callback would update _downloadState and notification:
            // { progress ->
            //     _downloadState.value = TileDownloadState.Downloading(
            //         regionId = regionId,
            //         regionName = regionName,
            //         completedResources = progress.completedResourceCount,
            //         requiredResources = progress.requiredResourceCount,
            //         completedBytes = progress.completedResourceSize,
            //         erroredResources = progress.erroredResourceCount
            //     )
            //     updateNotification(regionName, currentState.progressPercent)
            // }
            //
            // On completion:
            // { result ->
            //     if (result.isValue) {
            //         _downloadState.value = TileDownloadState.Completed(...)
            //     } else {
            //         _downloadState.value = TileDownloadState.Failed(...)
            //     }
            //     stopSelf()
            // }

            // Placeholder: Immediately mark as completed since there's no real SDK call
            _downloadState.value = TileDownloadState.Completed(
                regionId = regionId,
                regionName = regionName,
                totalBytes = 0
            )
            ServiceCompat.stopForeground(
                this@TileDownloadService,
                ServiceCompat.STOP_FOREGROUND_REMOVE
            )
            stopSelf()
        }
    }

    private fun pauseDownload() {
        downloadJob?.cancel()
        val currentState = _downloadState.value
        if (currentState is TileDownloadState.Downloading) {
            _downloadState.value = TileDownloadState.Paused(
                regionId = currentState.regionId,
                regionName = currentState.regionName,
                completedResources = currentState.completedResources,
                requiredResources = currentState.requiredResources
            )
            // TODO: Call cancelable?.cancel() on the Mapbox TileStore download
        }
    }

    private fun resumeDownload(regionId: String) {
        val currentState = _downloadState.value
        if (currentState is TileDownloadState.Paused) {
            // TODO: Re-call tileStore.loadTileRegion() with same region ID (auto-resumes)
            _downloadState.value = TileDownloadState.Downloading(
                regionId = currentState.regionId,
                regionName = currentState.regionName,
                completedResources = currentState.completedResources,
                requiredResources = currentState.requiredResources,
                completedBytes = 0,
                erroredResources = 0
            )
        }
    }

    private fun cancelDownload() {
        downloadJob?.cancel()
        _downloadState.value = TileDownloadState.Idle
        // TODO: Call cancelable?.cancel() on the Mapbox TileStore download
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    companion object {
        const val CHANNEL_ID = "tile_download_channel"
        const val CHANNEL_NAME = "Map Downloads"
        const val NOTIFICATION_ID = 1001
        const val NOTIFICATION_THROTTLE_MS = 1000L

        const val ACTION_START_DOWNLOAD = "com.patagonia.app.START_DOWNLOAD"
        const val ACTION_PAUSE_DOWNLOAD = "com.patagonia.app.PAUSE_DOWNLOAD"
        const val ACTION_RESUME_DOWNLOAD = "com.patagonia.app.RESUME_DOWNLOAD"
        const val ACTION_CANCEL_DOWNLOAD = "com.patagonia.app.CANCEL_DOWNLOAD"

        const val EXTRA_REGION_ID = "extra_region_id"
        const val EXTRA_REGION_NAME = "extra_region_name"
        const val EXTRA_WEST = "extra_west"
        const val EXTRA_SOUTH = "extra_south"
        const val EXTRA_EAST = "extra_east"
        const val EXTRA_NORTH = "extra_north"
        const val EXTRA_MIN_ZOOM = "extra_min_zoom"
        const val EXTRA_MAX_ZOOM = "extra_max_zoom"

        /** Shared state flow for UI observation of download progress */
        private val _downloadState = MutableStateFlow<TileDownloadState>(TileDownloadState.Idle)
        val downloadState: StateFlow<TileDownloadState> = _downloadState.asStateFlow()

        /**
         * Create an Intent to start a download.
         */
        fun createStartIntent(
            context: Context,
            regionId: String,
            regionName: String,
            west: Double,
            south: Double,
            east: Double,
            north: Double,
            minZoom: Int = 0,
            maxZoom: Int = 14
        ): Intent = Intent(context, TileDownloadService::class.java).apply {
            action = ACTION_START_DOWNLOAD
            putExtra(EXTRA_REGION_ID, regionId)
            putExtra(EXTRA_REGION_NAME, regionName)
            putExtra(EXTRA_WEST, west)
            putExtra(EXTRA_SOUTH, south)
            putExtra(EXTRA_EAST, east)
            putExtra(EXTRA_NORTH, north)
            putExtra(EXTRA_MIN_ZOOM, minZoom)
            putExtra(EXTRA_MAX_ZOOM, maxZoom)
        }

        fun createPauseIntent(context: Context): Intent =
            Intent(context, TileDownloadService::class.java).apply {
                action = ACTION_PAUSE_DOWNLOAD
            }

        fun createResumeIntent(context: Context, regionId: String): Intent =
            Intent(context, TileDownloadService::class.java).apply {
                action = ACTION_RESUME_DOWNLOAD
                putExtra(EXTRA_REGION_ID, regionId)
            }

        fun createCancelIntent(context: Context): Intent =
            Intent(context, TileDownloadService::class.java).apply {
                action = ACTION_CANCEL_DOWNLOAD
            }
    }
}
