package com.patagonia.app.presentation.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patagonia.app.domain.model.Capture
import com.patagonia.app.presentation.viewmodel.MapUiState
import com.patagonia.app.presentation.viewmodel.MapViewModel
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.CameraOptions


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
        onCaptureSelected = viewModel::selectCapture,
        onDismissTooltip = viewModel::dismissTooltip,
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
    onCaptureSelected: (Capture) -> Unit,
    onDismissTooltip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        val mapViewportState = rememberMapViewportState {
            setCameraOptions {
                center(Point.fromLngLat(uiState.cameraLongitude, uiState.cameraLatitude))
                zoom(uiState.cameraZoom)
            }
        }

        LaunchedEffect(uiState.cameraLatitude, uiState.cameraLongitude) {
            mapViewportState.easeTo(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(uiState.cameraLongitude, uiState.cameraLatitude))
                    .zoom(uiState.cameraZoom)
                    .build()
            )
        }

        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            style = { MapStyle(style = uiState.styleUri) }
        ) {
            // Draw interactive markers for each species capture on the map (D-23)
            uiState.captures.forEach { capture ->
                PointAnnotation(
                    point = Point.fromLngLat(capture.longitude, capture.latitude),
                    onClick = {
                        onCaptureSelected(capture)
                        true
                    }
                )
            }
        }

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

        // Tooltip popup for selected capture (D-23)
        AnimatedVisibility(
            visible = uiState.isTooltipVisible,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            uiState.selectedCapture?.let { capture ->
                CaptureTooltip(
                    capture = capture,
                    onDismiss = onDismissTooltip
                )
            }
        }
    }
}

/**
 * Tooltip popup card showing species info when a map pin is tapped (D-23).
 * Displays species name, scientific name, and a thumbnail placeholder.
 */
@Composable
fun CaptureTooltip(
    capture: Capture,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { /* Navigate to capture detail in future */ },
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail placeholder (will be replaced with actual image loading)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Species info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = capture.speciesName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                capture.scientificName?.let { scientificName ->
                    Text(
                        text = scientificName,
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "(${capture.latitude}, ${capture.longitude})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Dismiss button
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss tooltip",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
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
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Map View",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Style: $styleUri",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Center: ($latitude, $longitude)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Mapbox SDK pending — set MAPBOX_DOWNLOADS_TOKEN",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
