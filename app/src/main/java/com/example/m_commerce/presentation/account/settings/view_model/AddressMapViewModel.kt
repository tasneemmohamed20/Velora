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
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.data.services.location.LocationWorker
import com.example.m_commerce.domain.entities.Address
import com.example.m_commerce.domain.entities.AddressType
import com.example.m_commerce.domain.entities.CustomerAddresses
import com.example.m_commerce.domain.repository.IGeoCodingRepository
import com.example.m_commerce.domain.usecases.CustomerUseCase
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
    private val placesClient: PlacesClient,
    sharedPreferencesHelper: SharedPreferencesHelper,
    private val customerUseCase: CustomerUseCase
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

    private val customerId = sharedPreferencesHelper.getCustomerId()
    private val _updateAddressState = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val updateAddressState: StateFlow<ResponseState> = _updateAddressState

    private val _areAddressesFull = MutableStateFlow(false)
    val areAddressesFull: StateFlow<Boolean> = _areAddressesFull

    init {
        startLocationUpdates()
        observeLocationUpdates()
        getCustomerAddresses()
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


    // In AddressMapViewModel.kt

    fun saveAddressToCustomer(newAddress: Address) {
        viewModelScope.launch {
            _updateAddressState.value = ResponseState.Loading
            try {
                customerUseCase(customerId.toString()).collect { customer ->
                    val existingAddresses = customer.addresses?.firstOrNull() ?: CustomerAddresses("", "", "")

                    val addressString = buildString {
                        append("type:").append(newAddress.type.name)
                        append("|building:").append(newAddress.building)
                        append("|street:").append(newAddress.street)
                        append("|apt:").append(newAddress.apartment)
                        append("|floor:").append(newAddress.floor ?: "")
                        append("|area:").append(newAddress.area)
                        append("|phone:").append(newAddress.phoneNumber)
                        append("|label:").append(newAddress.addressLabel ?: "")
                        append("|directions:").append(newAddress.additionalDirections ?: "")
                        append("|lat:").append(newAddress.latitude)
                        append("|lon:").append(newAddress.longitude)
                    }

                    val updatedAddresses = if (_editingAddress.value != null) {
                        // Compare building and street instead of latitude
                        when {
                            existingAddresses.address1?.contains("building:${_editingAddress.value?.building}|street:${_editingAddress.value?.street}") == true ->
                                existingAddresses.copy(address1 = addressString)
                            existingAddresses.address2?.contains("building:${_editingAddress.value?.building}|street:${_editingAddress.value?.street}") == true ->
                                existingAddresses.copy(address2 = addressString)
                            else -> throw Exception("Could not find address to update")
                        }
                    } else {
                        // Add new address to the first empty slot
                        when {
                            existingAddresses.address1.isNullOrEmpty() ->
                                existingAddresses.copy(address1 = addressString)
                            existingAddresses.address2.isNullOrEmpty() ->
                                existingAddresses.copy(address2 = addressString)
                            else -> throw Exception("No empty address slots available")
                        }
                    }

                    Log.d(TAG, "Updating addresses: $updatedAddresses")

                    customerUseCase(
                        id = customerId,
                        phone = newAddress.phoneNumber,
                        addresses = updatedAddresses
                    ).collect { result ->
                        if (result.id.isNotEmpty()) {
                            _updateAddressState.value = ResponseState.Success(result)
                            getCustomerAddresses()
                        } else {
                            throw Exception("Failed to update customer data")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update address: ${e.message}", e)
                _updateAddressState.value = ResponseState.Failure(e)
            }
        }
    }

    fun getCustomerAddresses() {
        viewModelScope.launch {
            try {
                customerUseCase(customerId.toString()).collect { customer ->
                    Log.d(TAG, "Customer addresses: ${customer}")
                    val decodedAddresses = customer.addresses?.mapNotNull { customerAddress ->
                        listOfNotNull(
                            customerAddress.address1,
                            customerAddress.address2,
                            customerAddress.formatted
                        ).mapNotNull { addressStr ->
                            _areAddressesFull.value = !customerAddress.address1.isNullOrEmpty() &&
                                    !customerAddress.address2.isNullOrEmpty()
                            Log.d(TAG, "Are addresses full? ${_areAddressesFull.value}")
                            if (addressStr.isBlank()) return@mapNotNull null

                            try {
                                val parts = addressStr.split("|")
                                val map = parts.associate {
                                    val keyValue = it.split(":", limit = 2)
                                    if (keyValue.size == 2) keyValue[0] to keyValue[1] else "" to ""
                                }

                                val latitude = map["lat"]?.toDoubleOrNull() ?: return@mapNotNull null
                                val longitude = map["lon"]?.toDoubleOrNull() ?: return@mapNotNull null
                                _selectedLocation .value = LatLng(latitude, longitude)
                                Address(
                                    building = map["building"] ?: "",
                                    street = map["street"] ?: "",
                                    apartment = map["apt"] ?: "",
                                    floor = map["floor"] ?: "",
                                    area = map["area"] ?: "",
                                    additionalDirections = map["directions"] ?: "",
                                    latitude = latitude,
                                    longitude = longitude,
                                    type = AddressType.valueOf(map["type"] ?: AddressType.HOME.name),
                                    phoneNumber = map["phone"] ?: "",
                                    addressLabel = map["label"] ?: ""
                                )

                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to decode address: $addressStr", e)
                                null
                            }
                        }
                    }?.flatten() ?: emptyList()

                    _addresses.value = decodedAddresses
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get customer addresses", e)
                _addresses.value = emptyList()
            }
        }
    }
}