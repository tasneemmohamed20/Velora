package com.example.m_commerce.presentation.checkout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.BillingAddress
import com.example.m_commerce.domain.entities.Customer
import com.example.m_commerce.domain.usecases.CustomerUseCase
import com.example.m_commerce.domain.usecases.DraftOrderUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject


@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val customerUseCase: CustomerUseCase,
    private val draftOrderUseCase: DraftOrderUseCase
) : ViewModel() {

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation
    private val _selectedAddress = MutableStateFlow<String?>(null)
    val selectedAddress: StateFlow<String?> = _selectedAddress

    init {
        fetchCustomerAndSetLocation()
    }

    private fun fetchCustomerAndSetLocation() {
        viewModelScope.launch {
            val customerId = sharedPreferencesHelper.getCustomerId().toString()
            val customer: Flow<Customer> = customerUseCase(customerId)
            val addresses = customer?.firstOrNull()?.addresses.orEmpty()

            val homeAddress = addresses.find { it.address1?.contains("type:HOME", ignoreCase = true) == true }
            val selectedAddress = homeAddress ?: addresses.firstOrNull()
            val (lat, lon) = extractLatLng(selectedAddress?.address1)
            if (lat != null && lon != null) {
                _selectedLocation.value = LatLng(lat, lon)
                Log.d("CheckoutViewModel", "Selected location: ${_selectedLocation.value}")
            }
            val billingAddress = BillingAddress(
                address1 = selectedAddress?.address1,
                address2 = selectedAddress?.address2,
                city = selectedAddress?.city,
                phone = selectedAddress?.phone
            )
            updateDraftOrderBillingAddress(billingAddress)
        }
    }

    fun extractLatLng(address1: String?): Pair<Double?, Double?> {
        if (address1 == null) return Pair(null, null)
        val parts = address1.split("|")
        val lat = parts.find { it.startsWith("lat:") }?.removePrefix("lat:")?.toDoubleOrNull()
        val lon = parts.find { it.startsWith("lon:") }?.removePrefix("lon:")?.toDoubleOrNull()
        val area = parts.find { it.startsWith("area:") }?.removePrefix("area:")
        _selectedAddress.value = area
        return Pair(lat, lon)
    }
    
    private fun updateDraftOrderBillingAddress(address: BillingAddress) {
        viewModelScope.launch {
            val draftOrderId = sharedPreferencesHelper.getCartDraftOrderId().toString()
            draftOrderUseCase(draftOrderId, address)
            
        }
    }
}