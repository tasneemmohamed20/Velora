package com.example.m_commerce.data.services.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationHelper(internal val context: Context) {
    private val fusedClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val TAG = "LocationHelper"

    fun hasLocationPermission(): Boolean = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun getCurrentLocation(): Location? = suspendCoroutine { continuation ->
        if (!hasLocationPermission()) {
            continuation.resume(null)
            return@suspendCoroutine
        }

        try {
            val currentLocationRequest = CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMaxUpdateAgeMillis(1000)
                .build()

            fusedClient.getCurrentLocation(currentLocationRequest, null)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        continuation.resume(task.result)
                    } else {
                        Log.e(TAG, "Failed to get current location", task.exception)
                        continuation.resume(null)
                    }
                }
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception while getting location", e)
            continuation.resume(null)
        }
    }
}