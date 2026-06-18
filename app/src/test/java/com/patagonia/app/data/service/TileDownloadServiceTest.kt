package com.patagonia.app.data.service

import com.patagonia.app.domain.model.TileDownloadState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for TileDownloadService testable logic.
 *
 * NOTE: Service lifecycle methods (onCreate, onStartCommand) require Android context
 * and are tested via instrumentation tests. These tests cover the testable business
 * logic: companion constants, notification channel configuration, and state management.
 */
class TileDownloadServiceTest {

    @Test
    fun `notification channel ID is tile_download_channel`() {
        assertEquals("tile_download_channel", TileDownloadService.CHANNEL_ID)
    }

    @Test
    fun `notification channel name is Map Downloads`() {
        assertEquals("Map Downloads", TileDownloadService.CHANNEL_NAME)
    }

    @Test
    fun `notification throttle is 1 second`() {
        assertEquals(1000L, TileDownloadService.NOTIFICATION_THROTTLE_MS)
    }

    @Test
    fun `download state starts as Idle`() {
        val state = TileDownloadService.downloadState.value
        assertTrue("Initial state should be Idle", state is TileDownloadState.Idle)
    }

    @Test
    fun `action constants are properly namespaced`() {
        assertTrue(
            TileDownloadService.ACTION_START_DOWNLOAD.startsWith("com.patagonia.app.")
        )
        assertTrue(
            TileDownloadService.ACTION_PAUSE_DOWNLOAD.startsWith("com.patagonia.app.")
        )
        assertTrue(
            TileDownloadService.ACTION_RESUME_DOWNLOAD.startsWith("com.patagonia.app.")
        )
        assertTrue(
            TileDownloadService.ACTION_CANCEL_DOWNLOAD.startsWith("com.patagonia.app.")
        )
    }

    @Test
    fun `extra keys are distinct`() {
        val extras = listOf(
            TileDownloadService.EXTRA_REGION_ID,
            TileDownloadService.EXTRA_REGION_NAME,
            TileDownloadService.EXTRA_WEST,
            TileDownloadService.EXTRA_SOUTH,
            TileDownloadService.EXTRA_EAST,
            TileDownloadService.EXTRA_NORTH,
            TileDownloadService.EXTRA_MIN_ZOOM,
            TileDownloadService.EXTRA_MAX_ZOOM
        )
        assertEquals(extras.size, extras.distinct().size)
    }

    @Test
    fun `notification ID is stable for updates`() {
        assertEquals(1001, TileDownloadService.NOTIFICATION_ID)
    }
}
