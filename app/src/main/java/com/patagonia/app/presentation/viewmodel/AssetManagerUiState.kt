package com.patagonia.app.presentation.viewmodel

import com.patagonia.app.domain.model.DownloadedRegion
import com.patagonia.app.domain.model.TileDownloadState

/**
 * Represents the UI state for the Asset Manager screen.
 */
data class AssetManagerUiState(
    /** List of all downloaded regions */
    val downloadedRegions: List<DownloadedRegion> = emptyList(),
    /** Current active download state */
    val downloadState: TileDownloadState = TileDownloadState.Idle,
    /** Bounding box input fields for custom download */
    val customRegionName: String = "",
    val west: String = "",
    val south: String = "",
    val east: String = "",
    val north: String = "",
    val minZoom: Int = 0,
    val maxZoom: Int = 14,
    /** Warnings/Errors for validation */
    val validationWarning: String? = null,
    val validationError: String? = null,
    /** Loading/Action states */
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isDownloading: Boolean
        get() = downloadState is TileDownloadState.Downloading
}
