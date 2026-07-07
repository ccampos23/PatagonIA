package com.patagonia.app.data.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.patagonia.app.domain.repository.LocationResult
import com.patagonia.app.domain.repository.LocationTracker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationTrackerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationClient: FusedLocationProviderClient
) : LocationTracker {

    override suspend fun getCurrentLocation(): LocationResult? {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocation && !hasCoarseLocation) {
            return null
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val isGpsEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
        val isNetworkEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true

        if (!isGpsEnabled && !isNetworkEnabled) {
            return null
        }

        return suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()
            try {
                locationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        if (continuation.isActive) {
                            continuation.resume(LocationResult(location.latitude, location.longitude))
                        }
                    } else {
                        // Attempt fallback to last location
                        locationClient.lastLocation.addOnSuccessListener { lastLoc ->
                            if (continuation.isActive) {
                                if (lastLoc != null) {
                                    continuation.resume(LocationResult(lastLoc.latitude, lastLoc.longitude))
                                } else {
                                    continuation.resume(null)
                                }
                            }
                        }.addOnFailureListener {
                            if (continuation.isActive) continuation.resume(null)
                        }
                    }
                }.addOnFailureListener {
                    // Try fallback to last location as well
                    locationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        if (continuation.isActive) {
                            if (lastLoc != null) {
                                continuation.resume(LocationResult(lastLoc.latitude, lastLoc.longitude))
                            } else {
                                continuation.resume(null)
                            }
                        }
                    }.addOnFailureListener {
                        if (continuation.isActive) continuation.resume(null)
                    }
                }.addOnCanceledListener {
                    if (continuation.isActive) continuation.resume(null)
                }
            } catch (e: SecurityException) {
                if (continuation.isActive) continuation.resume(null)
            } catch (e: Exception) {
                if (continuation.isActive) continuation.resume(null)
            }

            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }
    }
}
