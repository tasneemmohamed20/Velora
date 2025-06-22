package com.example.m_commerce.presentation.account.settings.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.m_commerce.presentation.utils.ResponseState
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
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

    private val _isAddMode = MutableStateFlow(true)
    val isAddMode: StateFlow<Boolean> = _isAddMode

    private val _formState = MutableStateFlow<AddressFormState?>(null)

    init {
        startLocationUpdates()
        observeLocationUpdates()
        getCustomerAddresses()
    }

    fun resetForAddMode() {
        _isAddMode.value = true
        _editingAddress.value = null
        _phoneNumber.value = ""
    }

    // Setup for edit mode
    fun setupForEditMode(address: Address) {
        _isAddMode.value = false
        _editingAddress.value = address
        _phoneNumber.value = address.phoneNumber
        updateCurrentLocation(LatLng(address.latitude, address.longitude))
    }

    fun updateCurrentLocation(latLng: LatLng) {
        _currentLocation.value = latLng
        fetchAddress("${latLng.latitude},${latLng.longitude}")
    }

    fun confirmSelectedLocation(latLng: LatLng) {
        _selectedLocation.value = latLng
        _currentLocation.value = latLng
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

    fun storeFormState(
        addressType: AddressType,
        buildingName: String,
        aptNumber: String,
        floor: String,
        street: String,
        additionalDirections: String,
        addressLabel: String
    ) {
        _formState.value = AddressFormState(
            addressType,
            buildingName,
            aptNumber,
            floor,
            street,
            additionalDirections,
            addressLabel
        )
    }

    fun validateAndUpdatePhone(newValue: String) {
        if (newValue.isEmpty() || (newValue.all { it.isDigit() } && newValue.length <= 11)) {
            _phoneNumber.value = newValue
            _isPhoneValid.value = newValue.isEmpty() || newValue.matches("^01[0125][0-9]{8}$".toRegex())
        }
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


    fun saveAddressToCustomer(newAddress: Address) {
        viewModelScope.launch {
            _updateAddressState.value = ResponseState.Loading
            try {
                val customer = customerUseCase(customerId.toString()).first()
                val currentApiAddresses = customer.addresses?.toMutableList() ?: mutableListOf()

                val address1 = "type:${newAddress.type.name}|building:${newAddress.building}|street:${newAddress.street}|apt:${newAddress.apartment}|floor:${newAddress.floor ?: ""}|area:${newAddress.area}|lat:${newAddress.latitude}|lon:${newAddress.longitude}"
                val address2 = "label:${newAddress.addressLabel ?: ""}|directions:${newAddress.additionalDirections ?: ""}"

                val editingId = _editingAddress.value?.id
                Log.d(TAG, "Editing address ID: $editingId")
                if (editingId != null) {
                    val index = currentApiAddresses.indexOfFirst { it.id == editingId }
                    if (index != -1) {
                        currentApiAddresses[index] = currentApiAddresses[index].copy(
                            address1 = address1,
                            address2 = address2,
                            phone = newAddress.phoneNumber
                        )
                    } else {
                        throw Exception("Address with id $editingId not found for update.")
                    }
                } else {
                    val newApiAddress = CustomerAddresses(
                        id = UUID.randomUUID().toString(),
                        address1 = address1,
                        address2 = address2,
                        phone = newAddress.phoneNumber,
                        city = newAddress.area
                    )
                    currentApiAddresses.add(newApiAddress)
                }

                Log.d(TAG, "Updating addresses: $currentApiAddresses")

                customerUseCase(id = customerId.toString(), addresses = currentApiAddresses)
                    .collect { updatedCustomer ->
                        _updateAddressState.value = ResponseState.Success(updatedCustomer)
                        getCustomerAddresses()
                        _editingAddress.value = null
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save address: ${e.message}", e)
                _updateAddressState.value = ResponseState.Failure(e)
            }
        }
    }

    fun getCustomerAddresses() {
        viewModelScope.launch {
            _addresses.value = emptyList()
            try {
                customerUseCase(customerId.toString()).collect { customer ->
                    val uiAddresses = customer.addresses?.mapNotNull { apiAddress ->
                        try {
                            val address1Map = apiAddress.address1?.split("|")?.mapNotNull {
                                val pair = it.split(":", limit = 2)
                                if (pair.size == 2) pair[0] to pair[1] else null
                            }?.toMap() ?: emptyMap()

                            val address2Map = apiAddress.address2?.split("|")?.mapNotNull {
                                val pair = it.split(":", limit = 2)
                                if (pair.size == 2) pair[0] to pair[1] else null
                            }?.toMap() ?: emptyMap()

                            val lat = address1Map["lat"]?.toDoubleOrNull()
                            val lon = address1Map["lon"]?.toDoubleOrNull()

                            if (lat == null || lon == null) {
                                Log.w(TAG, "Skipping address with invalid lat/lon: ${apiAddress.id}")
                                return@mapNotNull null
                            }

                            Address(
                                id = apiAddress.id,
                                type = AddressType.valueOf(address1Map["type"] ?: AddressType.HOME.name),
                                building = address1Map["building"] ?: "",
                                street = address1Map["street"] ?: "",
                                apartment = address1Map["apt"] ?: "",
                                floor = address1Map["floor"] ?: "",
                                area = address1Map["area"] ?: "",
                                latitude = lat,
                                longitude = lon,
                                addressLabel = address2Map["label"] ?: "",
                                additionalDirections = address2Map["directions"] ?: "",
                                phoneNumber = apiAddress.phone ?: ""
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing address ${apiAddress.id}", e)
                            null
                        }
                    } ?: emptyList()

                    _addresses.value = uiAddresses
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get customer addresses", e)
                _addresses.value = emptyList()
            }
        }
    }

}

data class AddressFormState(
    val addressType: AddressType,
    val buildingName: String,
    val aptNumber: String,
    val floor: String,
    val street: String,
    val additionalDirections: String,
    val addressLabel: String
)
