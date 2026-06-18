package com.patagonia.app.domain.model

/**
 * Represents a downloaded offline map region.
 * Used by the Asset Manager UI to list downloaded regions and their status.
 */
data class DownloadedRegion(
    val id: String,
    val name: String,
    val sizeBytes: Long,
    val completedResources: Long,
    val requiredResources: Long,
    val isComplete: Boolean
) {
    val sizeMB: Double
        get() = sizeBytes / (1024.0 * 1024.0)
}
