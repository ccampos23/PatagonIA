package com.patagonia.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patagonia.app.domain.model.ParkRegionPreset
import com.patagonia.app.domain.model.TileDownloadState
import com.patagonia.app.domain.repository.TileDownloadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AssetManagerViewModel @Inject constructor(
    private val repository: TileDownloadRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssetManagerUiState())
    val uiState: StateFlow<AssetManagerUiState> = _uiState.asStateFlow()

    init {
        loadDownloadedRegions()
        observeDownloadState()
    }

    fun loadDownloadedRegions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val regions = repository.getDownloadedRegions()
                _uiState.update { it.copy(downloadedRegions = regions, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message, isLoading = false) }
            }
        }
    }

    private fun observeDownloadState() {
        viewModelScope.launch {
            repository.downloadState.collect { state ->
                _uiState.update { it.copy(downloadState = state) }
                // Reload list if download completed
                if (state is TileDownloadState.Completed) {
                    loadDownloadedRegions()
                }
            }
        }
    }

    fun startPresetDownload(preset: ParkRegionPreset) {
        viewModelScope.launch {
            try {
                repository.startDownload(
                    regionId = preset.id,
                    regionName = preset.nameEs,
                    west = preset.west,
                    south = preset.south,
                    east = preset.east,
                    north = preset.north,
                    minZoom = preset.minZoom,
                    maxZoom = preset.maxZoom
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun updateCustomRegionName(name: String) {
        _uiState.update { it.copy(customRegionName = name) }
    }

    fun updateCoordinates(west: String, south: String, east: String, north: String) {
        _uiState.update {
            it.copy(
                west = west,
                south = south,
                east = east,
                north = north
            )
        }
        validateCustomBoundingBox()
    }

    private fun validateCustomBoundingBox() {
        val state = _uiState.value
        val w = state.west.toDoubleOrNull()
        val s = state.south.toDoubleOrNull()
        val e = state.east.toDoubleOrNull()
        val n = state.north.toDoubleOrNull()

        if (w == null || s == null || e == null || n == null) {
            _uiState.update {
                it.copy(
                    validationWarning = null,
                    validationError = null
                )
            }
            return
        }

        val estimatedSizeMB = repository.estimateDownloadSizeMB(
            west = w,
            south = s,
            east = e,
            north = n,
            minZoom = state.minZoom,
            maxZoom = state.maxZoom
        )

        val warning = if (estimatedSizeMB > 100) {
            "El tamaño estimado (${estimatedSizeMB}MB) excede los 100MB recomendados. La descarga puede tomar tiempo y consumir almacenamiento."
        } else {
            null
        }

        // Hard block at 500MB (surrogate for Mapbox tile limits in offline mode / 750k tiles)
        val error = if (estimatedSizeMB > 500) {
            "La zona seleccionada es demasiado grande (${estimatedSizeMB}MB) y supera el límite máximo de descarga (equivalente a 750,000 teselas). Por favor reduce el área."
        } else {
            null
        }

        _uiState.update {
            it.copy(
                validationWarning = warning,
                validationError = error
            )
        }
    }

    fun startCustomDownload() {
        val state = _uiState.value
        if (state.validationError != null) return

        val w = state.west.toDoubleOrNull() ?: return
        val s = state.south.toDoubleOrNull() ?: return
        val e = state.east.toDoubleOrNull() ?: return
        val n = state.north.toDoubleOrNull() ?: return

        val regionId = UUID.randomUUID().toString()
        val regionName = state.customRegionName.ifBlank { "Custom Region" }

        viewModelScope.launch {
            try {
                repository.startDownload(
                    regionId = regionId,
                    regionName = regionName,
                    west = w,
                    south = s,
                    east = e,
                    north = n,
                    minZoom = state.minZoom,
                    maxZoom = state.maxZoom
                )
                // Reset inputs
                _uiState.update {
                    it.copy(
                        customRegionName = "",
                        west = "",
                        south = "",
                        east = "",
                        north = "",
                        validationWarning = null,
                        validationError = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun pauseDownload(regionId: String) {
        repository.pauseDownload(regionId)
    }

    fun resumeDownload(regionId: String) {
        viewModelScope.launch {
            try {
                repository.resumeDownload(regionId)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun cancelDownload(regionId: String) {
        repository.cancelDownload(regionId)
    }

    fun deleteRegion(regionId: String) {
        viewModelScope.launch {
            try {
                repository.deleteRegion(regionId)
                loadDownloadedRegions()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }
}
