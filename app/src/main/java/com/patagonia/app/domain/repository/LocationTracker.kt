package com.patagonia.app.domain.repository

/**
 * Abstraction for obtaining the device's current geographic location.
 * Implementations live in the data layer; the domain layer depends only on this interface.
 */
interface LocationTracker {

    /**
     * Attempts to retrieve the current device location.
     *
     * @return a [LocationResult] with latitude/longitude on success, or `null`
     *         when the location cannot be determined (e.g., permissions denied, GPS off).
     */
    suspend fun getCurrentLocation(): LocationResult?
}

/**
 * Lightweight value object carrying a geographic position.
 * Kept in the domain layer so that ViewModels and UseCases can reference it
 * without depending on Android framework classes.
 */
data class LocationResult(
    val latitude: Double,
    val longitude: Double
)
