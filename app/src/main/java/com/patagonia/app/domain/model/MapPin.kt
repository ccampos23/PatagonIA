package com.patagonia.app.domain.model

/**
 * Represents a pin on the map derived from a species capture.
 * Combines location data with the category-specific icon identifier (D-19).
 */
data class MapPin(
    val captureId: String,
    val speciesName: String,
    val latitude: Double,
    val longitude: Double,
    val category: SpeciesCategory,
    val confidence: Float?,
    val imagePath: String
) {
    companion object {
        /**
         * Create a MapPin from a Capture, inferring the species category.
         */
        fun fromCapture(capture: Capture): MapPin = MapPin(
            captureId = capture.id,
            speciesName = capture.speciesName,
            latitude = capture.latitude,
            longitude = capture.longitude,
            category = SpeciesCategory.fromSpeciesName(capture.speciesName),
            confidence = capture.confidence,
            imagePath = capture.imagePath
        )
    }
}
