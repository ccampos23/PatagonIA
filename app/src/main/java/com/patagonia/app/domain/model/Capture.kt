package com.patagonia.app.domain.model

data class Capture(
    val id: String,
    val speciesName: String,
    val scientificName: String?,
    val timestamp: Long,
    val imagePath: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double?,
    val confidence: Float?,
    val notes: String?,
    val syncStatus: SyncStatus = SyncStatus.PENDING_INSERT,
    val remoteId: String? = null,
    val isShared: Boolean = false,
    val isDeleted: Boolean = false,
    val userId: String? = null
)
