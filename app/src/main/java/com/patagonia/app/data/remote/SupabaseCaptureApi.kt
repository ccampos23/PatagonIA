package com.patagonia.app.data.remote

import com.patagonia.app.data.remote.dto.RemoteCaptureDto

interface SupabaseCaptureApi {
    suspend fun insertCapture(dto: RemoteCaptureDto): String
    suspend fun updateCapture(dto: RemoteCaptureDto)
    suspend fun softDeleteCapture(remoteId: String)
    suspend fun fetchAllCaptures(userId: String): List<RemoteCaptureDto>
    suspend fun updateSharingStatus(remoteId: String, isShared: Boolean)
}
