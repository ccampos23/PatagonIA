package com.patagonia.app.presentation.viewmodel

import com.patagonia.app.domain.model.Capture

/**
 * Represents the UI state for the map screen.
 */
data class MapUiState(
    /** Mapbox style URI - Outdoors v12 per D-10 */
    val styleUri: String = OUTDOORS_STYLE_URI,
    /** Camera center longitude (Chile default) */
    val cameraLongitude: Double = DEFAULT_LONGITUDE,
    /** Camera center latitude (Chile default) */
    val cameraLatitude: Double = DEFAULT_LATITUDE,
    /** Default zoom level */
    val cameraZoom: Double = DEFAULT_ZOOM,
    /** List of captures to display as pins */
    val captures: List<Capture> = emptyList(),
    /** Whether the map is showing the user's journal or global sightings (D-20) */
    val showMyJournal: Boolean = true,
    /** Compass orientation mode (D-12) */
    val isNorthUp: Boolean = true,
    /** Currently selected capture for tooltip popup (D-23), null when no tooltip shown */
    val selectedCapture: Capture? = null
) {
    /** Whether a tooltip popup should be displayed */
    val isTooltipVisible: Boolean get() = selectedCapture != null

    companion object {
        const val OUTDOORS_STYLE_URI = "mapbox://styles/mapbox/outdoors-v12"
        // Central Chile coordinates
        const val DEFAULT_LONGITUDE = -71.5
        const val DEFAULT_LATITUDE = -35.0
        const val DEFAULT_ZOOM = 6.0
    }
}

