package com.patagonia.app.domain.repository

import com.patagonia.app.domain.model.Capture
import kotlinx.coroutines.flow.Flow

interface CaptureRepository {

    fun getCaptures(): Flow<List<Capture>>

    fun getAllCaptures(): Flow<List<Capture>> = getCaptures()

    suspend fun getCaptureById(id: String): Capture?

    suspend fun saveCapture(capture: Capture)

    suspend fun addCapture(capture: Capture) = saveCapture(capture)

    suspend fun updateCapture(capture: Capture)

    suspend fun deleteCapture(id: String)

    suspend fun deleteCapture(capture: Capture) = deleteCapture(capture.id)

    fun getPendingSyncCaptures(): Flow<List<Capture>>

    fun getUnsyncedCaptures(): Flow<List<Capture>> = getPendingSyncCaptures()

    suspend fun toggleSharing(id: String, isShared: Boolean)

    fun getCaptureCount(): Flow<Int>
}
