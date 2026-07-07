package com.patagonia.app.data.repository

import com.patagonia.app.data.local.dao.CaptureDao
import com.patagonia.app.data.local.entity.CaptureEntity
import com.patagonia.app.domain.model.Capture
import com.patagonia.app.domain.model.SyncStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

import com.patagonia.app.domain.usecase.sync.EnqueueSyncUseCase

class CaptureRepositoryImplTest {

    private lateinit var captureDao: CaptureDao
    private lateinit var enqueueSyncUseCase: EnqueueSyncUseCase
    private lateinit var repository: CaptureRepositoryImpl

    @Before
    fun setup() {
        captureDao = mock()
        enqueueSyncUseCase = mock()
        repository = CaptureRepositoryImpl(captureDao, enqueueSyncUseCase)
    }

    @Test
    fun `saveCapture sets syncStatus to PENDING_INSERT`() = runTest {
        val capture = Capture(
            id = "test-1",
            speciesName = "Puma",
            scientificName = "Puma concolor",
            timestamp = 1000L,
            imagePath = "/img",
            latitude = -50.0,
            longitude = -73.0,
            altitude = null,
            confidence = 0.9f,
            notes = "",
            syncStatus = SyncStatus.SYNCED // set to SYNCED to verify it gets overridden
        )

        repository.saveCapture(capture)

        val captor = argumentCaptor<CaptureEntity>()
        verify(captureDao).insertCapture(captor.capture())
        assertEquals(SyncStatus.PENDING_INSERT, captor.firstValue.syncStatus)
    }

    @Test
    fun `updateCapture sets syncStatus to PENDING_UPDATE`() = runTest {
        val capture = Capture(
            id = "test-1",
            speciesName = "Puma",
            scientificName = "Puma concolor",
            timestamp = 1000L,
            imagePath = "/img",
            latitude = -50.0,
            longitude = -73.0,
            altitude = null,
            confidence = 0.9f,
            notes = "",
            syncStatus = SyncStatus.SYNCED
        )

        repository.updateCapture(capture)

        val captor = argumentCaptor<CaptureEntity>()
        verify(captureDao).updateCapture(captor.capture())
        assertEquals(SyncStatus.PENDING_UPDATE, captor.firstValue.syncStatus)
    }

    @Test
    fun `deleteCapture soft deletes using ID`() = runTest {
        repository.deleteCapture("test-1")

        verify(captureDao).softDelete("test-1")
    }

    @Test
    fun `getCaptures returns list mapped from DAO`() = runTest {
        val entities = listOf(
            CaptureEntity(
                id = "test-1",
                speciesName = "Puma",
                scientificName = "Puma concolor",
                timestamp = 1000L,
                imagePath = "/img",
                latitude = -50.0,
                longitude = -73.0,
                altitude = null,
                confidence = 0.9f,
                notes = "",
                syncStatus = SyncStatus.SYNCED
            )
        )
        whenever(captureDao.getAllCaptures()).thenReturn(flowOf(entities))

        val result = repository.getCaptures().first()

        assertEquals(1, result.size)
        assertEquals("test-1", result[0].id)
        assertEquals(SyncStatus.SYNCED, result[0].syncStatus)
    }

    @Test
    fun `toggleSharing updates sharing and sets PENDING_UPDATE`() = runTest {
        val entity = CaptureEntity(
            id = "test-1",
            speciesName = "Puma",
            scientificName = "Puma concolor",
            timestamp = 1000L,
            imagePath = "/img",
            latitude = -50.0,
            longitude = -73.0,
            altitude = null,
            confidence = 0.9f,
            notes = "",
            syncStatus = SyncStatus.SYNCED,
            isShared = false
        )
        whenever(captureDao.getCaptureById("test-1")).thenReturn(entity)

        repository.toggleSharing("test-1", true)

        val captor = argumentCaptor<CaptureEntity>()
        verify(captureDao).updateCapture(captor.capture())
        assertEquals(true, captor.firstValue.isShared)
        assertEquals(SyncStatus.PENDING_UPDATE, captor.firstValue.syncStatus)
    }
}
