package com.patagonia.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TileDownloadStateTest {

    @Test
    fun `Downloading progress is 0 when no resources required`() {
        val state = TileDownloadState.Downloading(
            regionId = "test",
            regionName = "Test Region",
            completedResources = 0,
            requiredResources = 0,
            completedBytes = 0,
            erroredResources = 0
        )
        assertEquals(0, state.progressPercent)
    }

    @Test
    fun `Downloading progress calculates correct percentage`() {
        val state = TileDownloadState.Downloading(
            regionId = "test",
            regionName = "Test Region",
            completedResources = 50,
            requiredResources = 100,
            completedBytes = 5000,
            erroredResources = 0
        )
        assertEquals(50, state.progressPercent)
    }

    @Test
    fun `Downloading progress is capped at 100`() {
        val state = TileDownloadState.Downloading(
            regionId = "test",
            regionName = "Test Region",
            completedResources = 150,
            requiredResources = 100,
            completedBytes = 15000,
            erroredResources = 0
        )
        assertEquals(100, state.progressPercent)
    }

    @Test
    fun `Downloading progress handles small fractions`() {
        val state = TileDownloadState.Downloading(
            regionId = "test",
            regionName = "Test Region",
            completedResources = 1,
            requiredResources = 1000,
            completedBytes = 100,
            erroredResources = 0
        )
        assertEquals(0, state.progressPercent) // 0.1% rounds down to 0
    }

    @Test
    fun `Completed state holds total bytes`() {
        val state = TileDownloadState.Completed(
            regionId = "region-1",
            regionName = "Torres del Paine",
            totalBytes = 50_000_000
        )
        assertEquals(50_000_000, state.totalBytes)
        assertEquals("Torres del Paine", state.regionName)
    }

    @Test
    fun `Failed state holds error message`() {
        val state = TileDownloadState.Failed(
            regionId = "region-1",
            regionName = "Test Region",
            error = "Disk full"
        )
        assertEquals("Disk full", state.error)
    }

    @Test
    fun `TimedOut state preserves progress for resume`() {
        val state = TileDownloadState.TimedOut(
            regionId = "region-1",
            regionName = "Torres del Paine",
            completedResources = 500,
            requiredResources = 1000
        )
        assertEquals(500, state.completedResources)
        assertEquals(1000, state.requiredResources)
    }
}
