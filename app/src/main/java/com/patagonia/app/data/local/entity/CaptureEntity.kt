package com.patagonia.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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
    val isSynced: Boolean = false
)
