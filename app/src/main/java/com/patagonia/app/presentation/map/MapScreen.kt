package com.patagonia.app.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patagonia.app.presentation.viewmodel.MapUiState
import com.patagonia.app.presentation.viewmodel.MapViewModel

/**
 * Map screen composable that displays the Mapbox map with trail overlays and capture pins.
 *
 * Architecture: Wraps native Mapbox MapView inside Compose (D-08).
 * When Mapbox SDK is available (dependencies uncommented with valid token),
 * the placeholder is replaced with the real MapboxMap composable.
 *
 * Style: Mapbox Outdoors v12 (D-10)
 * Offline: Blank grid if no tiles downloaded (D-11)
 */
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    MapScreenContent(
        uiState = uiState,
        onToggleJournal = viewModel::toggleJournalMode,
        onToggleCompass = viewModel::toggleCompassOrientation,
        modifier = modifier
    )
}

/**
 * Stateless content composable for the map screen.
 * State is hoisted to allow preview and testing.
 */
@Composable
fun MapScreenContent(
    uiState: MapUiState,
    onToggleJournal: () -> Unit,
    onToggleCompass: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // ──────────────────────────────────────────────────────
        // Map Area
        // When Mapbox SDK dependencies are enabled, replace this
        // placeholder with:
        //
        // val mapViewportState = rememberMapViewportState {
        //     setCameraOptions {
        //         center(Point.fromLngLat(uiState.cameraLongitude, uiState.cameraLatitude))
        //         zoom(uiState.cameraZoom)
        //     }
        // }
        // MapboxMap(
        //     modifier = Modifier.fillMaxSize(),
        //     mapViewportState = mapViewportState,
        //     style = { MapStyle(style = uiState.styleUri) }
        // ) {
        //     MapEffect(Unit) { mapView ->
        //         // Trail GeoJSON overlay, pin annotations, etc.
        //     }
        // }
        // ──────────────────────────────────────────────────────
        MapPlaceholder(
            styleUri = uiState.styleUri,
            latitude = uiState.cameraLatitude,
            longitude = uiState.cameraLongitude,
            modifier = Modifier.fillMaxSize()
        )

        // Map controls overlay
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            // Journal toggle (D-20)
            FilterChip(
                selected = uiState.showMyJournal,
                onClick = onToggleJournal,
                label = {
                    Text(if (uiState.showMyJournal) "My Journal" else "Global")
                }
            )

            // Compass toggle (D-12)
            IconButton(onClick = onToggleCompass) {
                Icon(
                    imageVector = if (uiState.isNorthUp) Icons.Default.Place else Icons.Default.LocationOn,
                    contentDescription = if (uiState.isNorthUp) "North-up mode" else "Heading-up mode",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Placeholder composable shown when Mapbox SDK is not yet linked.
 * Displays map configuration info for development verification.
 * In production with SDK enabled, this is replaced by the real MapboxMap.
 */
@Composable
private fun MapPlaceholder(
    styleUri: String,
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(Color(0xFFE8E8E8)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "🗺️ Map View",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Style: $styleUri",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "Center: ($latitude, $longitude)",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "Mapbox SDK pending — set MAPBOX_DOWNLOADS_TOKEN",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Red
            )
        }
    }
}
