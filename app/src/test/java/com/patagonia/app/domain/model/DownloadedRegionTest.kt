package com.patagonia.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DownloadedRegionTest {

    @Test
    fun `sizeMB converts bytes to megabytes correctly`() {
        val region = DownloadedRegion(
            id = "test",
            name = "Test Region",
            sizeBytes = 50 * 1024 * 1024, // 50 MB
            completedResources = 100,
            requiredResources = 100,
            isComplete = true
        )
        assertEquals(50.0, region.sizeMB, 0.01)
    }

    @Test
    fun `sizeMB handles zero bytes`() {
        val region = DownloadedRegion(
            id = "test",
            name = "Test Region",
            sizeBytes = 0,
            completedResources = 0,
            requiredResources = 100,
            isComplete = false
        )
        assertEquals(0.0, region.sizeMB, 0.01)
    }

    @Test
    fun `isComplete reflects download status`() {
        val incomplete = DownloadedRegion(
            id = "test",
            name = "Test",
            sizeBytes = 1000,
            completedResources = 50,
            requiredResources = 100,
            isComplete = false
        )
        assertFalse(incomplete.isComplete)

        val complete = incomplete.copy(
            completedResources = 100,
            isComplete = true
        )
        assertTrue(complete.isComplete)
    }
}
