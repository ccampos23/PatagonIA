package com.patagonia.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patagonia.app.domain.usecase.GetCapturesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Map screen.
 * Manages map configuration state and capture overlay data.
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    private val getCapturesUseCase: GetCapturesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        observeCaptures()
    }

    private var hasCenteredOnCapture = false

    private fun observeCaptures() {
        viewModelScope.launch {
            getCapturesUseCase().collect { captures ->
                _uiState.update { state ->
                    if (!hasCenteredOnCapture && captures.isNotEmpty()) {
                        hasCenteredOnCapture = true
                        val mostRecent = captures.first()
                        state.copy(
                            captures = captures,
                            cameraLatitude = mostRecent.latitude,
                            cameraLongitude = mostRecent.longitude
                        )
                    } else {
                        state.copy(captures = captures)
                    }
                }
            }
        }
    }

    /**
     * Toggle between My Journal and Global Sightings (D-20).
     */
    fun toggleJournalMode() {
        _uiState.update { it.copy(showMyJournal = !it.showMyJournal) }
    }

    /**
     * Toggle compass orientation between North-up and Heading-up (D-12).
     */
    fun toggleCompassOrientation() {
        _uiState.update { it.copy(isNorthUp = !it.isNorthUp) }
    }

    /**
     * Select a capture to show its tooltip popup on the map (D-23).
     */
    fun selectCapture(capture: com.patagonia.app.domain.model.Capture) {
        _uiState.update { it.copy(selectedCapture = capture) }
    }

    /**
     * Dismiss the currently visible tooltip popup.
     */
    fun dismissTooltip() {
        _uiState.update { it.copy(selectedCapture = null) }
    }
}
