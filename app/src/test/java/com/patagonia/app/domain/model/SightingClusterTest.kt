package com.patagonia.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [SightingCluster] and [DensityRange].
 * Verifies sparse filtering (D-21) and density range classification (D-22).
 */
class SightingClusterTest {

    // --- Sparse filtering (D-21) ---

    @Test
    fun `sighting with count below 10 is not visible`() {
        val cluster = createCluster(count = 5)
        assertFalse(cluster.isVisible)
    }

    @Test
    fun `sighting with count of 0 is not visible`() {
        val cluster = createCluster(count = 0)
        assertFalse(cluster.isVisible)
    }

    @Test
    fun `sighting with count of 9 is not visible`() {
        val cluster = createCluster(count = 9)
        assertFalse(cluster.isVisible)
    }

    @Test
    fun `sighting with count of 10 is visible`() {
        val cluster = createCluster(count = 10)
        assertTrue(cluster.isVisible)
    }

    @Test
    fun `sighting with count of 100 is visible`() {
        val cluster = createCluster(count = 100)
        assertTrue(cluster.isVisible)
    }

    // --- Density range classification (D-22) ---

    @Test
    fun `count 5 maps to SPARSE range`() {
        assertEquals(DensityRange.SPARSE, DensityRange.fromCount(5))
    }

    @Test
    fun `count 0 maps to SPARSE range`() {
        assertEquals(DensityRange.SPARSE, DensityRange.fromCount(0))
    }

    @Test
    fun `count 9 maps to SPARSE range`() {
        assertEquals(DensityRange.SPARSE, DensityRange.fromCount(9))
    }

    @Test
    fun `count 10 maps to LOW range`() {
        assertEquals(DensityRange.LOW, DensityRange.fromCount(10))
    }

    @Test
    fun `count 50 maps to LOW range`() {
        assertEquals(DensityRange.LOW, DensityRange.fromCount(50))
    }

    @Test
    fun `count 51 maps to MEDIUM range`() {
        assertEquals(DensityRange.MEDIUM, DensityRange.fromCount(51))
    }

    @Test
    fun `count 300 maps to MEDIUM range`() {
        assertEquals(DensityRange.MEDIUM, DensityRange.fromCount(300))
    }

    @Test
    fun `count 301 maps to HIGH range`() {
        assertEquals(DensityRange.HIGH, DensityRange.fromCount(301))
    }

    @Test
    fun `count 1000 maps to HIGH range`() {
        assertEquals(DensityRange.HIGH, DensityRange.fromCount(1000))
    }

    @Test
    fun `count 1001 maps to VERY_HIGH range`() {
        assertEquals(DensityRange.VERY_HIGH, DensityRange.fromCount(1001))
    }

    @Test
    fun `count 5000 maps to VERY_HIGH range`() {
        assertEquals(DensityRange.VERY_HIGH, DensityRange.fromCount(5000))
    }

    // --- SightingCluster density range integration ---

    @Test
    fun `cluster densityRange matches count classification`() {
        val clusterLow = createCluster(count = 25)
        assertEquals(DensityRange.LOW, clusterLow.densityRange)

        val clusterMedium = createCluster(count = 150)
        assertEquals(DensityRange.MEDIUM, clusterMedium.densityRange)

        val clusterHigh = createCluster(count = 500)
        assertEquals(DensityRange.HIGH, clusterHigh.densityRange)
    }

    @Test
    fun `DensityRange labels are human-readable`() {
        assertEquals("Sparse", DensityRange.SPARSE.label)
        assertEquals("Low", DensityRange.LOW.label)
        assertEquals("Medium", DensityRange.MEDIUM.label)
        assertEquals("High", DensityRange.HIGH.label)
        assertEquals("Very High", DensityRange.VERY_HIGH.label)
    }

    // --- Filter helper ---

    @Test
    fun `filterVisible returns only clusters with count gte 10`() {
        val clusters = listOf(
            createCluster(count = 3),
            createCluster(count = 10),
            createCluster(count = 50),
            createCluster(count = 7)
        )
        val visible = clusters.filter { it.isVisible }
        assertEquals(2, visible.size)
        assertTrue(visible.all { it.count >= SightingCluster.MINIMUM_VISIBLE_COUNT })
    }

    // --- Helper ---

    private fun createCluster(count: Int) = SightingCluster(
        latitude = -33.4,
        longitude = -70.6,
        count = count,
        speciesName = "Test Species"
    )
}
