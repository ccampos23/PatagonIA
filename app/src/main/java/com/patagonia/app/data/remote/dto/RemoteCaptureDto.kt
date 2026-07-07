package com.patagonia.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RemoteCaptureDto(
    val id: String,
    val user_id: String,
    val species_name: String,
    val image_path: String,
    val latitude: Double,
    val longitude: Double,
    val captured_at: Long,
    val confidence: Float? = null,
    val notes: String = "",
    val is_shared: Boolean = false,
    val is_deleted: Boolean = false
)
