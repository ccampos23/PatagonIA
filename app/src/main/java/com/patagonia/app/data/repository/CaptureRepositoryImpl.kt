package com.patagonia.app.data.repository

import com.patagonia.app.data.local.dao.CaptureDao
import com.patagonia.app.data.local.entity.toDomain
import com.patagonia.app.data.local.entity.toEntity
import com.patagonia.app.domain.model.Capture
import com.patagonia.app.domain.repository.CaptureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CaptureRepositoryImpl @Inject constructor(
    private val captureDao: CaptureDao
) : CaptureRepository {

    override fun getAllCaptures(): Flow<List<Capture>> =
        captureDao.getAllCaptures().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getCaptureById(id: String): Capture? =
        captureDao.getCaptureById(id)?.toDomain()

    override suspend fun addCapture(capture: Capture) {
        captureDao.insertCapture(capture.toEntity())
    }

    override suspend fun updateCapture(capture: Capture) {
        captureDao.updateCapture(capture.toEntity())
    }

    override suspend fun deleteCapture(capture: Capture) {
        captureDao.deleteCapture(capture.toEntity())
    }

    override fun getUnsyncedCaptures(): Flow<List<Capture>> =
        captureDao.getUnsyncedCaptures().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getCaptureCount(): Flow<Int> =
        captureDao.getCaptureCount()
}
