package com.patagonia.app.domain.repository

import com.patagonia.app.domain.model.Capture
import kotlinx.coroutines.flow.Flow

interface CaptureRepository {

    fun getAllCaptures(): Flow<List<Capture>>

    suspend fun getCaptureById(id: String): Capture?

    suspend fun addCapture(capture: Capture)

    suspend fun updateCapture(capture: Capture)

    suspend fun deleteCapture(capture: Capture)

    fun getUnsyncedCaptures(): Flow<List<Capture>>

    fun getCaptureCount(): Flow<Int>
}
