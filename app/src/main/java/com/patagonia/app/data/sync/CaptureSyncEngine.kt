package com.patagonia.app.data.sync

import com.patagonia.app.data.local.dao.CaptureDao
import com.patagonia.app.data.local.entity.CaptureEntity
import com.patagonia.app.data.remote.SupabaseCaptureDataSource
import com.patagonia.app.data.remote.dto.RemoteCaptureDto
import com.patagonia.app.domain.model.SyncStatus
import com.patagonia.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CaptureSyncEngine @Inject constructor(
    private val captureDao: CaptureDao,
    private val supabaseCaptureDataSource: SupabaseCaptureDataSource,
    private val authRepository: AuthRepository
) {

    suspend fun pushCaptures(): SyncResult {
        return try {
            val pending = captureDao.getPendingSyncCaptures().first()
            var pushed = 0
            var failed = 0
            val errors = mutableListOf<SyncError>()

            for (capture in pending) {
                try {
                    val dto = capture.toRemoteDto()
                    when (capture.syncStatus) {
                        SyncStatus.PENDING_INSERT -> {
                            val remoteId = supabaseCaptureDataSource.insertCapture(dto)
                            captureDao.markAsSynced(capture.id, remoteId)
                            pushed++
                        }
                        SyncStatus.PENDING_UPDATE -> {
                            supabaseCaptureDataSource.updateCapture(dto)
                            captureDao.markAsSynced(capture.id, capture.remoteId ?: capture.id)
                            pushed++
                        }
                        SyncStatus.PENDING_DELETE -> {
                            val rId = capture.remoteId
                            if (rId != null) {
                                supabaseCaptureDataSource.softDeleteCapture(rId)
                            }
                            captureDao.markAsSynced(capture.id, rId ?: capture.id)
                            pushed++
                        }
                        else -> { /* No-op for SYNCED or SYNC_FAILED */ }
                    }
                } catch (e: Exception) {
                    failed++
                    errors.add(SyncError(capture.id, e.message ?: "Push failed", e))
                    captureDao.markAsSyncFailed(capture.id)
                }
            }

            if (errors.isNotEmpty()) {
                SyncResult.PartialSuccess(pushed, 0, failed, errors)
            } else {
                SyncResult.Success(pushed, 0, 0)
            }
        } catch (e: Exception) {
            SyncResult.Failure(e)
        }
    }

    suspend fun pullCaptures(isFreshInstall: Boolean): SyncResult {
        return try {
            val user = authRepository.getCurrentUser()
                ?: return SyncResult.Failure(IllegalStateException("No authenticated user session"))
            
            val remoteCaptures = supabaseCaptureDataSource.fetchAllCaptures(user.id)
            var pulled = 0

            for (dto in remoteCaptures) {
                val existing = captureDao.getByRemoteId(dto.id) ?: captureDao.getCaptureById(dto.id)
                if (isFreshInstall || existing == null) {
                    captureDao.insertCapture(dto.toLocalEntity(existing?.id))
                    pulled++
                } else {
                    // Reconcile: update only if already SYNCED locally (no pending changes)
                    if (existing.syncStatus == SyncStatus.SYNCED) {
                        captureDao.insertCapture(dto.toLocalEntity(existing.id))
                        pulled++
                    }
                    // If local has PENDING_* changes, skip to respect local-wins (D-09)
                }
            }

            SyncResult.Success(0, pulled, 0)
        } catch (e: Exception) {
            SyncResult.Failure(e)
        }
    }

    suspend fun toggleSharingRemote(captureId: String, isShared: Boolean) {
        val capture = captureDao.getCaptureById(captureId)
        val remoteId = capture?.remoteId
        if (remoteId != null) {
            supabaseCaptureDataSource.updateSharingStatus(remoteId, isShared)
        }
    }

    private fun CaptureEntity.toRemoteDto(): RemoteCaptureDto {
        return RemoteCaptureDto(
            id = remoteId ?: id,
            user_id = userId ?: "",
            species_name = speciesName,
            image_path = imagePath,
            latitude = latitude,
            longitude = longitude,
            captured_at = timestamp,
            confidence = confidence,
            notes = notes ?: "",
            is_shared = isShared,
            is_deleted = isDeleted
        )
    }

    private fun RemoteCaptureDto.toLocalEntity(localId: String? = null): CaptureEntity {
        return CaptureEntity(
            id = localId ?: id,
            speciesName = species_name,
            scientificName = null,
            timestamp = captured_at,
            imagePath = image_path,
            latitude = latitude,
            longitude = longitude,
            altitude = null,
            confidence = confidence,
            notes = notes,
            syncStatus = SyncStatus.SYNCED,
            remoteId = id,
            isShared = is_shared,
            isDeleted = is_deleted,
            userId = user_id
        )
    }
}
