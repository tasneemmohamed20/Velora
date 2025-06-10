package com.example.m_commerce.data.services.location

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    private val locationHelper = LocationHelper(context)
    private val TAG = "LocationWorker"

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val location = locationHelper.getCurrentLocation()

            if (location != null) {
                val outputData = workDataOf(
                    "latitude" to location.latitude,
                    "longitude" to location.longitude
                )
                Log.d(TAG, "Location obtained: ${location.latitude}, ${location.longitude}")
                Result.success(outputData)
            } else {
                Log.e(TAG, "Failed to get location")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting location", e)
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "LocationWork"
    }
}