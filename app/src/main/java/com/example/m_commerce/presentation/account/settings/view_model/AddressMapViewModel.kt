package com.example.m_commerce.presentation.account.settings.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.m_commerce.ResponseState
import com.example.m_commerce.data.services.location.LocationWorker
import com.example.m_commerce.domain.entities.Address
import com.example.m_commerce.domain.repository.IGeoCodingRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class AddressMapViewModel @Inject constructor (
    @ApplicationContext context: Context,
    private val geoCodingRepository: IGeoCodingRepository,
    private val placesClient: PlacesClient
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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val searchResults: StateFlow<List<AutocompletePrediction>> = _searchResults

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber

    private val _isPhoneValid = MutableStateFlow(true)
    val isPhoneValid: StateFlow<Boolean> = _isPhoneValid

    private val _editingAddress = MutableStateFlow<Address?>(null)
    val editingAddress: StateFlow<Address?> = _editingAddress

    init {
        startLocationUpdates()
        observeLocationUpdates()
    }

    fun updateCurrentLocation(latLng: LatLng) {
        _currentLocation.value = latLng
        fetchAddress("${latLng.latitude},${latLng.longitude}")
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

    fun onQueryChange(query: String) {
        _searchQuery.value = query
        if (query.length >= 3) {
            viewModelScope.launch {
                try {
                    val request = FindAutocompletePredictionsRequest.builder()
                        .setQuery(query)
                        .build()

                    suspendCoroutine<List<AutocompletePrediction>> { continuation ->
                        placesClient.findAutocompletePredictions(request)
                            .addOnSuccessListener { response ->
                                continuation.resume(response.autocompletePredictions)
                            }
                            .addOnFailureListener { exception ->
                                continuation.resumeWithException(exception)
                            }
                    }.let { predictions ->
                        _searchResults.value = predictions
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Search failed: ${e.message}")
                    _searchResults.value = emptyList()
                }
            }
        } else {
            _searchResults.value = emptyList()
        }
    }

    fun getPlaceDetails(placeId: String, onLocationFound: (LatLng) -> Unit) {
        viewModelScope.launch {
            try {
                val placeFields = listOf(Place.Field.LAT_LNG)
                val request = FetchPlaceRequest.newInstance(placeId, placeFields)

                suspendCoroutine<Place> { continuation ->
                    placesClient.fetchPlace(request)
                        .addOnSuccessListener { response ->
                            continuation.resume(response.place)
                        }
                        .addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                }.latLng?.let { latLng ->
                    onLocationFound(latLng)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get place details: ${e.message}")
            }
        }
    }


    fun clearQuery() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }

    fun addAddressFromInfo(address: Address) {
        _addresses.update { currentList -> currentList + address }
    }

    fun validateAndUpdatePhone(newValue: String) {
        if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.length <= 11)) {
            _phoneNumber.value = newValue
            _isPhoneValid.value = newValue.isEmpty() || newValue.matches("^01[0125][0-9]{8}$".toRegex())
        }
    }

    fun isValidEgyptianMobileNumber(number: String): Boolean {
        return number.matches("^01[0125][0-9]{8}$".toRegex())
    }

    fun setEditingAddress(address: Address?) {
        _editingAddress.value = address
        address?.let {
            _currentLocation.value = LatLng(it.latitude, it.longitude)
        }
    }

    fun updateSelectedLocation(latLng: LatLng) {
        _selectedLocation.value = latLng
        fetchAddress("${latLng.latitude},${latLng.longitude}")
    }

    fun updateOrAddAddress(newAddress: Address) {
        _addresses.update { currentList ->
            val existingAddress = _editingAddress.value
            if (existingAddress != null) {
                // Replace existing address
                currentList.map { address ->
                    if (address == existingAddress) newAddress else address
                }
            } else {
                // Add new address
                currentList + newAddress
            }
        }
        // Clear editing state
        _editingAddress.value = null
    }
}