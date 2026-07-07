package com.patagonia.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patagonia.app.domain.model.Capture
import com.patagonia.app.domain.usecase.AddCaptureUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import com.patagonia.app.domain.model.Recognition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

import com.patagonia.app.domain.repository.LocationTracker
import com.patagonia.app.domain.model.SyncStatus

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val addCaptureUseCase: AddCaptureUseCase,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _recognitions = MutableStateFlow<List<Recognition>>(emptyList())
    val recognitions: StateFlow<List<Recognition>> = _recognitions.asStateFlow()

    fun updateRecognitions(newRecognitions: List<Recognition>) {
        _recognitions.value = newRecognitions
    }

    fun saveCapture(speciesName: String, scientificName: String?, notes: String?, imagePath: String, confidence: Float? = null) {
        viewModelScope.launch {
            val captureId = UUID.randomUUID().toString()
            val timestamp = System.currentTimeMillis()
            
            val location = try {
                locationTracker.getCurrentLocation()
            } catch (e: Exception) {
                null
            }

            val latitude = location?.latitude ?: -45.57
            val longitude = location?.longitude ?: -72.06

            val capture = Capture(
                id = captureId,
                speciesName = speciesName.trim(),
                scientificName = scientificName?.trim() ?: "Species indet.",
                timestamp = timestamp,
                imagePath = imagePath,
                latitude = latitude,
                longitude = longitude,
                altitude = null,
                confidence = confidence,
                notes = notes?.trim(),
                syncStatus = SyncStatus.PENDING_INSERT
            )

            addCaptureUseCase(capture)
        }
    }
}
