package com.patagonia.app.data.remote

import com.patagonia.app.data.remote.dto.RemoteCaptureDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SupabaseCaptureDataSourceTest {

    private lateinit var api: SupabaseCaptureApi
    private lateinit var dataSource: SupabaseCaptureDataSource

    @Before
    fun setup() {
        api = mock()
        dataSource = SupabaseCaptureDataSource(api)
    }

    @Test
    fun `insertCapture sends correct fields and returns remote UUID`() = runTest {
        val dto = RemoteCaptureDto(
            id = "local-uuid",
            user_id = "user-uuid",
            species_name = "Puma",
            image_path = "/path/to/image.jpg",
            latitude = -50.0,
            longitude = -73.0,
            captured_at = 1000L,
            confidence = 0.95f,
            notes = "Spotted near the base towers",
            is_shared = false,
            is_deleted = false
        )
        whenever(api.insertCapture(dto)).thenReturn("remote-uuid-123")

        val result = dataSource.insertCapture(dto)

        assertEquals("remote-uuid-123", result)
        verify(api).insertCapture(dto)
    }

    @Test
    fun `updateCapture updates existing remote capture by ID`() = runTest {
        val dto = RemoteCaptureDto(
            id = "remote-uuid-123",
            user_id = "user-uuid",
            species_name = "Puma Updated",
            image_path = "/path/to/image.jpg",
            latitude = -50.0,
            longitude = -73.0,
            captured_at = 1000L,
            confidence = 0.95f,
            notes = "Spotted near the base towers",
            is_shared = true,
            is_deleted = false
        )

        dataSource.updateCapture(dto)

        verify(api).updateCapture(dto)
    }

    @Test
    fun `softDeleteCapture sets is_deleted true on remote`() = runTest {
        dataSource.softDeleteCapture("remote-uuid-123")

        verify(api).softDeleteCapture("remote-uuid-123")
    }

    @Test
    fun `fetchAllCaptures retrieves all captures for current user`() = runTest {
        val expected = listOf(
            RemoteCaptureDto(
                id = "remote-uuid-123",
                user_id = "user-uuid",
                species_name = "Puma",
                image_path = "/path/to/image.jpg",
                latitude = -50.0,
                longitude = -73.0,
                captured_at = 1000L,
                confidence = 0.95f,
                notes = "Spotted near the base towers",
                is_shared = false,
                is_deleted = false
            )
        )
        whenever(api.fetchAllCaptures("user-uuid")).thenReturn(expected)

        val result = dataSource.fetchAllCaptures("user-uuid")

        assertEquals(expected, result)
        verify(api).fetchAllCaptures("user-uuid")
    }

    @Test
    fun `updateSharingStatus immediately updates isShared flag`() = runTest {
        dataSource.updateSharingStatus("remote-uuid-123", true)

        verify(api).updateSharingStatus("remote-uuid-123", true)
    }
}
