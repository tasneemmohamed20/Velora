package com.example.m_commerce.presentation.account.settings.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.m_commerce.ResponseState
import com.example.m_commerce.data.services.location.LocationWorker
import com.example.m_commerce.domain.repository.IGeoCodingRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AddressMapViewModel(
    context: Context,
    private val geoCodingRepository: IGeoCodingRepository
) : ViewModel() {
    private val TAG = "AddressMapViewModel"
    private val workManager = WorkManager.getInstance(context)

    private val _locationState = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val locationState: StateFlow<ResponseState> = _locationState

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation

    private val _address = MutableStateFlow<String>("")
    val address: StateFlow<String> = _address

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        startLocationUpdates()
        observeLocationUpdates()
    }

    private fun startLocationUpdates() {
        val locationWork = OneTimeWorkRequestBuilder<LocationWorker>()
            .build()

        workManager.enqueueUniqueWork(
            LocationWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            locationWork
        )
    }

    private fun observeLocationUpdates() {
        workManager.getWorkInfosForUniqueWorkLiveData(LocationWorker.WORK_NAME)
            .observeForever { workInfoList ->
                val workInfo = workInfoList?.firstOrNull()
                when (workInfo?.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        val latitude = workInfo.outputData.getDouble("latitude", 0.0)
                        val longitude = workInfo.outputData.getDouble("longitude", 0.0)
                        val location = LatLng(latitude, longitude)
                        _currentLocation.value = location
                        _locationState.value = ResponseState.Success(location)
                        fetchAddress("$latitude,$longitude")
                    }
                    WorkInfo.State.FAILED -> {
                        Log.e(TAG, "Location work failed")
                        _locationState.value = ResponseState.Failure(Exception("Failed to get location"))
                    }
                    else -> {
                        _locationState.value = ResponseState.Loading
                    }
                }
            }
    }

    fun fetchAddress(latLng: String) {
        _isLoading.value = true
        viewModelScope.launch {
            geoCodingRepository.getAddressFromGeocoding(latLng, "")
                .catch { e ->
                    _address.value = "Error fetching address: ${e.message}"
                    _isLoading.value = false
                }
                .collect { address ->
                    _address.value = address
                    _isLoading.value = false
                    Log.d(TAG, "Address: $address")
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        workManager.cancelUniqueWork(LocationWorker.WORK_NAME)
    }

    class Factory(
        private val context: Context,
        private val geoCodingRepository: IGeoCodingRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddressMapViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddressMapViewModel(context, geoCodingRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}