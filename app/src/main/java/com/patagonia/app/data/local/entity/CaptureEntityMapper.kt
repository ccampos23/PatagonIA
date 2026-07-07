package com.patagonia.app.data.local.entity

import com.patagonia.app.domain.model.Capture

fun CaptureEntity.toDomain(): Capture = Capture(
    id = id,
    speciesName = speciesName,
    scientificName = scientificName,
    timestamp = timestamp,
    imagePath = imagePath,
    latitude = latitude,
    longitude = longitude,
    altitude = altitude,
    confidence = confidence,
    notes = notes,
    syncStatus = syncStatus,
    remoteId = remoteId,
    isShared = isShared,
    isDeleted = isDeleted,
    userId = userId
)

fun Capture.toEntity(): CaptureEntity = CaptureEntity(
    id = id,
    speciesName = speciesName,
    scientificName = scientificName,
    timestamp = timestamp,
    imagePath = imagePath,
    latitude = latitude,
    longitude = longitude,
    altitude = altitude,
    confidence = confidence,
    notes = notes,
    syncStatus = syncStatus,
    remoteId = remoteId,
    isShared = isShared,
    isDeleted = isDeleted,
    userId = userId
)
