package com.patagonia.app.domain.model

/**
 * Represents a cluster of crowdsourced sightings at a geographic location.
 * Used to aggregate and filter global sightings on the map (D-21, D-22).
 */
data class SightingCluster(
    /** Cluster center latitude */
    val latitude: Double,
    /** Cluster center longitude */
    val longitude: Double,
    /** Number of sightings in this cluster */
    val count: Int,
    /** Representative species name for display */
    val speciesName: String
) {
    /**
     * Density range classification for visual rendering (D-22).
     * Easily changeable thresholds: 10-50, 50-300, 300-1000+
     */
    val densityRange: DensityRange
        get() = DensityRange.fromCount(count)

    /**
     * Whether this cluster should be visible on the map.
     * Sparse sightings (count < 10) are filtered out (D-21).
     */
    val isVisible: Boolean
        get() = count >= MINIMUM_VISIBLE_COUNT

    companion object {
        /** Minimum sighting count to display a cluster (D-21) */
        const val MINIMUM_VISIBLE_COUNT = 10
    }
}

/**
 * Density range for visual clustering on the map (D-22).
 * Each range maps to a distinct visual style (size, color, opacity).
 */
enum class DensityRange(val label: String, val minCount: Int, val maxCount: Int) {
    /** Below visibility threshold — should not be rendered */
    SPARSE("Sparse", 0, 9),
    /** Low density: 10-50 sightings */
    LOW("Low", 10, 50),
    /** Medium density: 51-300 sightings */
    MEDIUM("Medium", 51, 300),
    /** High density: 301-1000 sightings */
    HIGH("High", 301, 1000),
    /** Very high density: 1000+ sightings */
    VERY_HIGH("Very High", 1001, Int.MAX_VALUE);

    companion object {
        /**
         * Classify a sighting count into a density range.
         */
        fun fromCount(count: Int): DensityRange = when {
            count <= 9 -> SPARSE
            count <= 50 -> LOW
            count <= 300 -> MEDIUM
            count <= 1000 -> HIGH
            else -> VERY_HIGH
        }
    }
}
