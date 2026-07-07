package com.patagonia.app.data.repository

import com.patagonia.app.data.local.dao.CaptureDao
import com.patagonia.app.data.local.entity.toDomain
import com.patagonia.app.data.local.entity.toEntity
import com.patagonia.app.domain.model.Capture
import com.patagonia.app.domain.repository.CaptureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

import com.patagonia.app.domain.model.SyncStatus

import com.patagonia.app.domain.usecase.sync.EnqueueSyncUseCase

class CaptureRepositoryImpl @Inject constructor(
    private val captureDao: CaptureDao,
    private val enqueueSyncUseCase: EnqueueSyncUseCase
) : CaptureRepository {

    override fun getCaptures(): Flow<List<Capture>> =
        captureDao.getAllCaptures().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getCaptureById(id: String): Capture? =
        captureDao.getCaptureById(id)?.toDomain()

    override suspend fun saveCapture(capture: Capture) {
        val entity = capture.copy(syncStatus = SyncStatus.PENDING_INSERT).toEntity()
        captureDao.insertCapture(entity)
        enqueueSyncUseCase()
    }

    override suspend fun updateCapture(capture: Capture) {
        val entity = capture.copy(syncStatus = SyncStatus.PENDING_UPDATE).toEntity()
        captureDao.updateCapture(entity)
        enqueueSyncUseCase()
    }

    override suspend fun deleteCapture(id: String) {
        captureDao.softDelete(id)
        enqueueSyncUseCase()
    }

    override fun getPendingSyncCaptures(): Flow<List<Capture>> =
        captureDao.getPendingSyncCaptures().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun toggleSharing(id: String, isShared: Boolean) {
        val capture = captureDao.getCaptureById(id)
        if (capture != null) {
            val updated = capture.copy(
                isShared = isShared,
                syncStatus = SyncStatus.PENDING_UPDATE
            )
            captureDao.updateCapture(updated)
            enqueueSyncUseCase()
        }
    }

    override fun getCaptureCount(): Flow<Int> =
        captureDao.getCaptureCount()
}
