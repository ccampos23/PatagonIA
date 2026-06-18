package com.patagonia.app.domain.model

/**
 * Pre-defined Chilean national park region for offline map download (D-15).
 * Bounding boxes are static geographic facts — compile-time safe.
 */
data class ParkRegionPreset(
    val id: String,
    val nameEs: String,
    val nameEn: String,
    val region: String,
    val west: Double,
    val south: Double,
    val east: Double,
    val north: Double,
    val minZoom: Int = 0,
    val maxZoom: Int = 14,
    val estimatedSizeMB: Int
) {
    companion object {
        /**
         * Curated list of Chilean national parks with bounding boxes.
         * Sizes are estimates based on vector tile benchmarks.
         */
        val CHILEAN_PARKS: List<ParkRegionPreset> = listOf(
            ParkRegionPreset(
                id = "torres-del-paine",
                nameEs = "Torres del Paine",
                nameEn = "Torres del Paine",
                region = "Magallanes",
                west = -73.5, south = -51.3, east = -72.7, north = -50.7,
                estimatedSizeMB = 75
            ),
            ParkRegionPreset(
                id = "conguillio",
                nameEs = "Conguillío",
                nameEn = "Conguillío",
                region = "La Araucanía",
                west = -71.8, south = -38.8, east = -71.5, north = -38.6,
                estimatedSizeMB = 25
            ),
            ParkRegionPreset(
                id = "villarrica",
                nameEs = "Villarrica",
                nameEn = "Villarrica",
                region = "La Araucanía",
                west = -72.1, south = -39.6, east = -71.7, north = -39.25,
                estimatedSizeMB = 30
            ),
            ParkRegionPreset(
                id = "queulat",
                nameEs = "Queulat",
                nameEn = "Queulat",
                region = "Aysén",
                west = -72.6, south = -44.6, east = -72.0, north = -44.1,
                estimatedSizeMB = 40
            ),
            ParkRegionPreset(
                id = "puyehue",
                nameEs = "Puyehue",
                nameEn = "Puyehue",
                region = "Los Ríos",
                west = -72.3, south = -40.85, east = -71.8, north = -40.5,
                estimatedSizeMB = 30
            ),
            ParkRegionPreset(
                id = "nahuelbuta",
                nameEs = "Nahuelbuta",
                nameEn = "Nahuelbuta",
                region = "La Araucanía",
                west = -73.15, south = -37.9, east = -72.8, north = -37.65,
                estimatedSizeMB = 20
            ),
            ParkRegionPreset(
                id = "lauca",
                nameEs = "Lauca",
                nameEn = "Lauca",
                region = "Arica y Parinacota",
                west = -69.65, south = -18.45, east = -69.03, north = -18.05,
                estimatedSizeMB = 35
            )
        )

        /** Warning threshold in MB per D-18 */
        const val SIZE_WARNING_THRESHOLD_MB = 100

        /** Hard limit on tile count per Mapbox TileStore limits (D-18) */
        const val TILE_COUNT_HARD_LIMIT = 750_000L
    }
}
