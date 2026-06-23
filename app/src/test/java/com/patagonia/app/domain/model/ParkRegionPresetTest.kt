package com.patagonia.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ParkRegionPresetTest {

    @Test
    fun `Chilean parks list contains 7 parks`() {
        assertEquals(7, ParkRegionPreset.CHILEAN_PARKS.size)
    }

    @Test
    fun `Torres del Paine is in the preset list`() {
        val tdp = ParkRegionPreset.CHILEAN_PARKS.find { it.id == "torres-del-paine" }
        assertTrue("Torres del Paine should exist", tdp != null)
        assertEquals("Magallanes", tdp!!.region)
    }

    @Test
    fun `all parks have valid bounding boxes where west is less than east`() {
        ParkRegionPreset.CHILEAN_PARKS.forEach { park ->
            assertTrue(
                "${park.nameEn}: west (${park.west}) should be < east (${park.east})",
                park.west < park.east
            )
            assertTrue(
                "${park.nameEn}: south (${park.south}) should be < north (${park.north})",
                park.south < park.north
            )
        }
    }

    @Test
    fun `all parks have positive estimated size`() {
        ParkRegionPreset.CHILEAN_PARKS.forEach { park ->
            assertTrue(
                "${park.nameEn}: estimated size should be > 0",
                park.estimatedSizeMB > 0
            )
        }
    }

    @Test
    fun `default zoom range is 0 to 14`() {
        ParkRegionPreset.CHILEAN_PARKS.forEach { park ->
            assertEquals(0, park.minZoom)
            assertEquals(14, park.maxZoom)
        }
    }

    @Test
    fun `size warning threshold is 100MB per D-18`() {
        assertEquals(100, ParkRegionPreset.SIZE_WARNING_THRESHOLD_MB)
    }

    @Test
    fun `tile count hard limit is 750000 per D-18`() {
        assertEquals(750_000L, ParkRegionPreset.TILE_COUNT_HARD_LIMIT)
    }

    @Test
    fun `all park IDs are unique`() {
        val ids = ParkRegionPreset.CHILEAN_PARKS.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
    }
}
