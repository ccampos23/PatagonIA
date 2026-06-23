package com.patagonia.app.domain.model

/**
 * Represents the state of an offline map tile download.
 * Used by the Foreground Service to report progress and by the UI to display status.
 */
sealed class TileDownloadState {
    data object Idle : TileDownloadState()

    data class Downloading(
        val regionId: String,
        val regionName: String,
        val completedResources: Long,
        val requiredResources: Long,
        val completedBytes: Long,
        val erroredResources: Long
    ) : TileDownloadState() {
        val progressPercent: Int
            get() = if (requiredResources > 0) {
                ((completedResources.toDouble() / requiredResources) * 100).toInt()
                    .coerceIn(0, 100)
            } else 0
    }

    data class Paused(
        val regionId: String,
        val regionName: String,
        val completedResources: Long,
        val requiredResources: Long
    ) : TileDownloadState()

    data class Completed(
        val regionId: String,
        val regionName: String,
        val totalBytes: Long
    ) : TileDownloadState()

    data class Failed(
        val regionId: String,
        val regionName: String,
        val error: String
    ) : TileDownloadState()

    /**
     * Android 15 timeout state - service must stop within seconds (D-17).
     * Checkpoint data is saved for resume.
     */
    data class TimedOut(
        val regionId: String,
        val regionName: String,
        val completedResources: Long,
        val requiredResources: Long
    ) : TileDownloadState()
}
