package com.patagonia.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

import com.patagonia.app.domain.model.SyncStatus

@Entity(tableName = "captures")
data class CaptureEntity(
    @PrimaryKey val id: String,
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
