package com.patagonia.app.domain.repository

import kotlinx.coroutines.flow.Flow

sealed interface DownloadStatus {
    object Idle : DownloadStatus
    data class Progress(val percentage: Int) : DownloadStatus
    object Success : DownloadStatus
    data class Error(val message: String) : DownloadStatus
}

interface ModelDownloader {
    fun downloadModelFiles(modelUrl: String, labelsUrl: String): Flow<DownloadStatus>
    fun isModelDownloaded(): Boolean
    fun markModelDownloaded(downloaded: Boolean)
}
