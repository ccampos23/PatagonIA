package com.patagonia.app.domain.model

/**
 * Represents a hiking trail loaded from GeoJSON data.
 * Pre-packaged Chilean trails per D-13.
 */
data class Trail(
    val id: String,
    val name: String,
    val difficulty: String,
    /** GeoJSON coordinate pairs [[lng, lat], ...] */
    val coordinates: List<Pair<Double, Double>>
)
