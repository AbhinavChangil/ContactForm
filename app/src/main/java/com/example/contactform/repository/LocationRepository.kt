// LocationRepository.kt
package com.example.contactform.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationRepository(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun getCurrentLocation(
        onLocationFetched: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            onError("Location permission not granted")
            return
        }

        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val latLong = "${location.latitude},${location.longitude}"
                        onLocationFetched(latLong)
                    } else {
                        onError("Failed to get location. Try again.")
                    }
                }
                .addOnFailureListener { exception ->
                    onError("Error fetching location: ${exception.localizedMessage}")
                }
        } catch (e: SecurityException) {
            Log.e("LocationRepository", "Location permission required.", e)
            onError("SecurityException: Location permission required.")
        }
    }
}
