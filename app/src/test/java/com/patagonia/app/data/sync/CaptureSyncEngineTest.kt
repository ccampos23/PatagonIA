package com.patagonia.app.data.sync

import com.patagonia.app.data.local.dao.CaptureDao
import com.patagonia.app.data.local.entity.CaptureEntity
import com.patagonia.app.data.remote.SupabaseCaptureDataSource
import com.patagonia.app.data.remote.dto.RemoteCaptureDto
import com.patagonia.app.domain.model.SyncStatus
import com.patagonia.app.domain.model.UserProfile
import com.patagonia.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CaptureSyncEngineTest {

    private lateinit var captureDao: CaptureDao
    private lateinit var supabaseCaptureDataSource: SupabaseCaptureDataSource
    private lateinit var authRepository: AuthRepository
    private lateinit var syncEngine: CaptureSyncEngine

    @Before
    fun setup() {
        captureDao = mock()
        supabaseCaptureDataSource = mock()
        authRepository = mock()
        syncEngine = CaptureSyncEngine(captureDao, supabaseCaptureDataSource, authRepository)
    }

    @Test
    fun `pushCaptures inserts PENDING_INSERT and updates status`() = runTest {
        val pendingInsert = CaptureEntity(
            id = "local-1", speciesName = "Puma", scientificName = null,
            timestamp = 1000L, imagePath = "", latitude = 0.0, longitude = 0.0,
            altitude = null, confidence = null, notes = null,
            syncStatus = SyncStatus.PENDING_INSERT, userId = "user-123"
        )
        whenever(captureDao.getPendingSyncCaptures()).thenReturn(flowOf(listOf(pendingInsert)))
        whenever(supabaseCaptureDataSource.insertCapture(any())).thenReturn("remote-uuid-1")

        val result = syncEngine.pushCaptures()

        assertTrue(result is SyncResult.Success)
        val success = result as SyncResult.Success
        assertEquals(1, success.pushed)
        assertEquals(0, success.failed)
        verify(supabaseCaptureDataSource).insertCapture(any())
        verify(captureDao).markAsSynced("local-1", "remote-uuid-1")
    }

    @Test
    fun `pushCaptures updates PENDING_UPDATE and updates status`() = runTest {
        val pendingUpdate = CaptureEntity(
            id = "local-1", speciesName = "Puma", scientificName = null,
            timestamp = 1000L, imagePath = "", latitude = 0.0, longitude = 0.0,
            altitude = null, confidence = null, notes = null,
            syncStatus = SyncStatus.PENDING_UPDATE, remoteId = "remote-uuid-1", userId = "user-123"
        )
        whenever(captureDao.getPendingSyncCaptures()).thenReturn(flowOf(listOf(pendingUpdate)))

        val result = syncEngine.pushCaptures()

        assertTrue(result is SyncResult.Success)
        val success = result as SyncResult.Success
        assertEquals(1, success.pushed)
        verify(supabaseCaptureDataSource).updateCapture(any())
        verify(captureDao).markAsSynced("local-1", "remote-uuid-1")
    }

    @Test
    fun `pushCaptures deletes PENDING_DELETE and updates status`() = runTest {
        val pendingDelete = CaptureEntity(
            id = "local-1", speciesName = "Puma", scientificName = null,
            timestamp = 1000L, imagePath = "", latitude = 0.0, longitude = 0.0,
            altitude = null, confidence = null, notes = null,
            syncStatus = SyncStatus.PENDING_DELETE, remoteId = "remote-uuid-1", userId = "user-123"
        )
        whenever(captureDao.getPendingSyncCaptures()).thenReturn(flowOf(listOf(pendingDelete)))

        val result = syncEngine.pushCaptures()

        assertTrue(result is SyncResult.Success)
        val success = result as SyncResult.Success
        assertEquals(1, success.pushed)
        verify(supabaseCaptureDataSource).softDeleteCapture("remote-uuid-1")
        verify(captureDao).markAsSynced("local-1", "remote-uuid-1")
    }

    @Test
    fun `pushCaptures quarantines failed capture and continues syncing others`() = runTest {
        val badCapture = CaptureEntity(
            id = "local-bad", speciesName = "Bad Sighting", scientificName = null,
            timestamp = 1000L, imagePath = "", latitude = 0.0, longitude = 0.0,
            altitude = null, confidence = null, notes = null,
            syncStatus = SyncStatus.PENDING_INSERT, userId = "user-123"
        )
        val goodCapture = CaptureEntity(
            id = "local-good", speciesName = "Condor", scientificName = null,
            timestamp = 1001L, imagePath = "", latitude = 0.0, longitude = 0.0,
            altitude = null, confidence = null, notes = null,
            syncStatus = SyncStatus.PENDING_INSERT, userId = "user-123"
        )
        whenever(captureDao.getPendingSyncCaptures()).thenReturn(flowOf(listOf(badCapture, goodCapture)))
        whenever(supabaseCaptureDataSource.insertCapture(any()))
            .thenThrow(RuntimeException("Network failure"))
            .thenReturn("remote-uuid-good")

        val result = syncEngine.pushCaptures()

        assertTrue(result is SyncResult.PartialSuccess)
        val partial = result as SyncResult.PartialSuccess
        assertEquals(1, partial.pushed)
        assertEquals(1, partial.failed)
        verify(captureDao).markAsSyncFailed("local-bad")
        verify(captureDao).markAsSynced("local-good", "remote-uuid-good")
    }

    @Test
    fun `pullCaptures adopts all remote captures on fresh install`() = runTest {
        val user = UserProfile(id = "user-123", email = "test@user.com", username = "tester")
        whenever(authRepository.getCurrentUser()).thenReturn(user)
        val remoteCaptures = listOf(
            RemoteCaptureDto(
                id = "remote-1", user_id = "user-123", species_name = "Puma",
                image_path = "/path", latitude = -50.0, longitude = -73.0,
                captured_at = 1000L, confidence = 0.9f, notes = "",
                is_shared = false, is_deleted = false
            )
        )
        whenever(supabaseCaptureDataSource.fetchAllCaptures("user-123")).thenReturn(remoteCaptures)

        val result = syncEngine.pullCaptures(isFreshInstall = true)

        assertTrue(result is SyncResult.Success)
        val success = result as SyncResult.Success
        assertEquals(1, success.pulled)
        verify(captureDao).insertCapture(any())
    }

    @Test
    fun `pullCaptures respects local-wins strategy`() = runTest {
        val user = UserProfile(id = "user-123", email = "test@user.com", username = "tester")
        whenever(authRepository.getCurrentUser()).thenReturn(user)
        val remoteCaptures = listOf(
            RemoteCaptureDto(
                id = "remote-1", user_id = "user-123", species_name = "Puma Server Version",
                image_path = "/path", latitude = -50.0, longitude = -73.0,
                captured_at = 1000L, confidence = 0.9f, notes = "",
                is_shared = false, is_deleted = false
            )
        )
        whenever(supabaseCaptureDataSource.fetchAllCaptures("user-123")).thenReturn(remoteCaptures)

        // Existing local capture with pending updates
        val localCapture = CaptureEntity(
            id = "local-1", speciesName = "Puma Local Version", scientificName = null,
            timestamp = 1000L, imagePath = "", latitude = 0.0, longitude = 0.0,
            altitude = null, confidence = null, notes = null,
            syncStatus = SyncStatus.PENDING_UPDATE, remoteId = "remote-1", userId = "user-123"
        )
        whenever(captureDao.getByRemoteId("remote-1")).thenReturn(localCapture)

        val result = syncEngine.pullCaptures(isFreshInstall = false)

        assertTrue(result is SyncResult.Success)
        verify(captureDao, never()).insertCapture(any())
        verify(captureDao, never()).updateCapture(any())
    }

    @Test
    fun `pullCaptures updates from remote if local is already SYNCED`() = runTest {
        val user = UserProfile(id = "user-123", email = "test@user.com", username = "tester")
        whenever(authRepository.getCurrentUser()).thenReturn(user)
        val remoteCaptures = listOf(
            RemoteCaptureDto(
                id = "remote-1", user_id = "user-123", species_name = "Puma Server Updated",
                image_path = "/path", latitude = -50.0, longitude = -73.0,
                captured_at = 1000L, confidence = 0.9f, notes = "Updated notes from server",
                is_shared = false, is_deleted = false
            )
        )
        whenever(supabaseCaptureDataSource.fetchAllCaptures("user-123")).thenReturn(remoteCaptures)

        val localCapture = CaptureEntity(
            id = "local-1", speciesName = "Puma Local Original", scientificName = null,
            timestamp = 1000L, imagePath = "", latitude = 0.0, longitude = 0.0,
            altitude = null, confidence = null, notes = "",
            syncStatus = SyncStatus.SYNCED, remoteId = "remote-1", userId = "user-123"
        )
        whenever(captureDao.getByRemoteId("remote-1")).thenReturn(localCapture)

        val result = syncEngine.pullCaptures(isFreshInstall = false)

        assertTrue(result is SyncResult.Success)
        val success = result as SyncResult.Success
        assertEquals(1, success.pulled)
        verify(captureDao).insertCapture(any()) // Room insert with REPLACE functions as update
    }

    @Test
    fun `toggleSharingRemote immediately updates is_shared flag on remote`() = runTest {
        val localCapture = CaptureEntity(
            id = "local-1", speciesName = "Puma", scientificName = null,
            timestamp = 1000L, imagePath = "", latitude = 0.0, longitude = 0.0,
            altitude = null, confidence = null, notes = "",
            syncStatus = SyncStatus.SYNCED, remoteId = "remote-uuid-1", userId = "user-123"
        )
        whenever(captureDao.getCaptureById("local-1")).thenReturn(localCapture)

        syncEngine.toggleSharingRemote("local-1", true)

        verify(supabaseCaptureDataSource).updateSharingStatus("remote-uuid-1", true)
    }
}
