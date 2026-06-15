package com.patagonia.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patagonia.app.domain.model.Capture
import com.patagonia.app.domain.usecase.AddCaptureUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val addCaptureUseCase: AddCaptureUseCase
) : ViewModel() {

    fun saveCapture(speciesName: String, notes: String?, imagePath: String) {
        viewModelScope.launch {
            val captureId = UUID.randomUUID().toString()
            val timestamp = System.currentTimeMillis()
            
            val mockLatitude = -45.5712 + (Math.random() - 0.5) * 0.1
            val mockLongitude = -72.0685 + (Math.random() - 0.5) * 0.1

            val capture = Capture(
                id = captureId,
                speciesName = speciesName.trim(),
                scientificName = getScientificNamePlaceholder(speciesName),
                timestamp = timestamp,
                imagePath = imagePath,
                latitude = mockLatitude,
                longitude = mockLongitude,
                altitude = null,
                confidence = 1.0f,
                notes = notes?.trim(),
                isSynced = false
            )

            addCaptureUseCase(capture)
        }
    }

    private fun getScientificNamePlaceholder(species: String): String {
        return when (species.lowercase().trim()) {
            "huemul" -> "Hippocamelus bisulcus"
            "condor", "cóndor" -> "Vultur gryphus"
            "puma" -> "Puma concolor"
            "guanaco" -> "Lama guanicoe"
            "coihue" -> "Nothofagus dombeyi"
            "araucaria" -> "Araucaria araucana"
            else -> "Species indet."
        }
    }
}
