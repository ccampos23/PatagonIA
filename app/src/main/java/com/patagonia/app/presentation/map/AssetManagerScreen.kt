package com.patagonia.app.presentation.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patagonia.app.domain.model.DownloadedRegion
import com.patagonia.app.domain.model.ParkRegionPreset
import com.patagonia.app.domain.model.TileDownloadState
import com.patagonia.app.presentation.theme.extendedColors
import com.patagonia.app.presentation.viewmodel.AssetManagerUiState
import com.patagonia.app.presentation.viewmodel.AssetManagerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetManagerScreen(
    viewModel: AssetManagerViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestor de Mapas Sin Conexión") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        AssetManagerContent(
            uiState = uiState,
            onPresetDownload = viewModel::startPresetDownload,
            onCustomNameChange = viewModel::updateCustomRegionName,
            onCoordinatesChange = viewModel::updateCoordinates,
            onCustomDownload = viewModel::startCustomDownload,
            onDeleteRegion = viewModel::deleteRegion,
            onPauseDownload = viewModel::pauseDownload,
            onResumeDownload = viewModel::resumeDownload,
            onCancelDownload = viewModel::cancelDownload,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
    }
}

@Composable
fun AssetManagerContent(
    uiState: AssetManagerUiState,
    onPresetDownload: (ParkRegionPreset) -> Unit,
    onCustomNameChange: (String) -> Unit,
    onCoordinatesChange: (String, String, String, String) -> Unit,
    onCustomDownload: () -> Unit,
    onDeleteRegion: (String) -> Unit,
    onPauseDownload: (String) -> Unit,
    onResumeDownload: (String) -> Unit,
    onCancelDownload: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Current active download progress card
        if (uiState.downloadState !is TileDownloadState.Idle) {
            item {
                ActiveDownloadCard(
                    state = uiState.downloadState,
                    onPause = onPauseDownload,
                    onResume = onResumeDownload,
                    onCancel = onCancelDownload
                )
            }
        }

        // Custom Bounding Box Download Tool
        item {
            CustomDownloadCard(
                uiState = uiState,
                onNameChange = onCustomNameChange,
                onCoordinatesChange = onCoordinatesChange,
                onDownloadClick = onCustomDownload
            )
        }

        // Chilean Presets List
        item {
            Text(
                text = "Parques Nacionales (Preestablecidos)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items(ParkRegionPreset.CHILEAN_PARKS) { preset ->
            PresetParkCard(
                preset = preset,
                isDownloading = uiState.isDownloading,
                onDownloadClick = { onPresetDownload(preset) }
            )
        }

        // Downloaded Regions List
        item {
            Text(
                text = "Mapas Guardados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (uiState.downloadedRegions.isEmpty()) {
            item {
                EmptyStateCard()
            }
        } else {
            items(uiState.downloadedRegions) { region ->
                DownloadedRegionCard(
                    region = region,
                    onDeleteClick = { onDeleteRegion(region.id) }
                )
            }
        }
    }
}

@Composable
fun ActiveDownloadCard(
    state: TileDownloadState,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
    onCancel: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            when (state) {
                is TileDownloadState.Downloading -> {
                    Text(
                        text = "Descargando: ${state.regionName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { state.progressPercent / 100f },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${state.progressPercent}% completado",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { onPause(state.regionId) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Pausar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onCancel(state.regionId) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
                is TileDownloadState.Paused -> {
                    Text(
                        text = "Descarga Pausada: ${state.regionName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { onResume(state.regionId) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Reanudar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onCancel(state.regionId) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
                is TileDownloadState.Failed -> {
                    Text(
                        text = "Error de descarga: ${state.regionName}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "No se pudo completar la descarga. Revisa tu conexión e inténtalo nuevamente.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onCancel(state.regionId) },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Cerrar")
                    }
                }
                is TileDownloadState.TimedOut -> {
                    Text(
                        text = "Descarga detenida (Límite de tiempo): ${state.regionName}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Se guardó el progreso de la descarga.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { onResume(state.regionId) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Reanudar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onCancel(state.regionId) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Cerrar")
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun CustomDownloadCard(
    uiState: AssetManagerUiState,
    onNameChange: (String) -> Unit,
    onCoordinatesChange: (String, String, String, String) -> Unit,
    onDownloadClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Descargar Área Personalizada",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.customRegionName,
                onValueChange = onNameChange,
                label = { Text("Nombre del Mapa") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = uiState.west,
                    onValueChange = { onCoordinatesChange(it, uiState.south, uiState.east, uiState.north) },
                    label = { Text("Oeste (Long)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = uiState.south,
                    onValueChange = { onCoordinatesChange(uiState.west, it, uiState.east, uiState.north) },
                    label = { Text("Sur (Lat)") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = uiState.east,
                    onValueChange = { onCoordinatesChange(uiState.west, uiState.south, it, uiState.north) },
                    label = { Text("Este (Long)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = uiState.north,
                    onValueChange = { onCoordinatesChange(uiState.west, uiState.south, uiState.east, it) },
                    label = { Text("Norte (Lat)") },
                    modifier = Modifier.weight(1f)
                )
            }

            AnimatedVisibility(visible = uiState.validationWarning != null) {
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "Advertencia", tint = MaterialTheme.extendedColors.warning)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.validationWarning ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.extendedColors.warning
                    )
                }
            }

            AnimatedVisibility(visible = uiState.validationError != null) {
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.validationError ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDownloadClick,
                enabled = uiState.validationError == null && !uiState.isDownloading &&
                        uiState.west.isNotBlank() && uiState.south.isNotBlank() &&
                        uiState.east.isNotBlank() && uiState.north.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Descargar región")
            }
        }
    }
}

@Composable
fun PresetParkCard(
    preset: ParkRegionPreset,
    isDownloading: Boolean,
    onDownloadClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = preset.nameEs,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${preset.region} • ~${preset.estimatedSizeMB} MB",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = onDownloadClick,
                enabled = !isDownloading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Descargar")
            }
        }
    }
}

@Composable
fun EmptyStateCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Aún no hay mapas descargados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Descarga una región para usar el mapa sin conexión.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DownloadedRegionCard(
    region: DownloadedRegion,
    onDeleteClick: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = region.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = String.format("%.1f MB", region.sizeMB),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }

            AnimatedVisibility(visible = showDeleteConfirm) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "¿Eliminar este mapa sin conexión? Puedes volver a descargarlo cuando quieras.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { showDeleteConfirm = false },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onDeleteClick()
                                showDeleteConfirm = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Confirmar")
                        }
                    }
                }
            }
        }
    }
}
