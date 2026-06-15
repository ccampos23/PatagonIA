package com.patagonia.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patagonia.app.domain.repository.DownloadStatus
import com.patagonia.app.domain.repository.ModelDownloader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val modelDownloader: ModelDownloader
) : ViewModel() {

    private val _downloadStatus = MutableStateFlow<DownloadStatus>(DownloadStatus.Idle)
    val downloadStatus: StateFlow<DownloadStatus> = _downloadStatus.asStateFlow()

    private val defaultModelUrl = "https://raw.githubusercontent.com/ccampos23/PatagonIA/main/models/species_model.tflite"
    private val defaultLabelsUrl = "https://raw.githubusercontent.com/ccampos23/PatagonIA/main/models/species_labels.txt"

    init {
        checkDownloadStatus()
    }

    fun checkDownloadStatus() {
        if (modelDownloader.isModelDownloaded()) {
            _downloadStatus.value = DownloadStatus.Success
        } else {
            _downloadStatus.value = DownloadStatus.Idle
        }
    }

    fun startDownload(
        modelUrl: String = defaultModelUrl,
        labelsUrl: String = defaultLabelsUrl
    ) {
        viewModelScope.launch {
            modelDownloader.downloadModelFiles(modelUrl, labelsUrl).collect { status ->
                _downloadStatus.value = status
            }
        }
    }
}
