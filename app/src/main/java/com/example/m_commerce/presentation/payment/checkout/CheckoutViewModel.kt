package com.example.m_commerce.presentation.checkout

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.BillingAddress
import com.example.m_commerce.domain.entities.Customer
import com.example.m_commerce.domain.entities.DiscountCodes
import com.example.m_commerce.domain.usecases.CompleteDraftOrder
import com.example.m_commerce.domain.usecases.CustomerUseCase
import com.example.m_commerce.domain.usecases.DiscountCodesUsecse
import com.example.m_commerce.domain.usecases.DraftOrderUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.Response
import javax.inject.Inject


@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val customerUseCase: CustomerUseCase,
    private val draftOrderUseCase: DraftOrderUseCase,
    private val completeDraftOrder: CompleteDraftOrder,
    private val discountCodesUseCase: DiscountCodesUsecse
) : ViewModel() {

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation
    private val _selectedAddress = MutableStateFlow<String?>(null)
    val selectedAddress: StateFlow<String?> = _selectedAddress
    private val _discountCodes = MutableStateFlow<List<DiscountCodes>>(emptyList())
    val discountCodes: StateFlow<List<DiscountCodes>> = _discountCodes.asStateFlow()

    private val _voucherText = MutableStateFlow("")
    val voucherText: StateFlow<String> = _voucherText.asStateFlow()

    private val _voucherError = MutableStateFlow<String?>(null)
    val voucherError: StateFlow<String?> = _voucherError.asStateFlow()

    private val _appliedDiscount = MutableStateFlow(0.0)
    val appliedDiscount: StateFlow<Double> = _appliedDiscount.asStateFlow()

    private val _isApplyingVoucher = MutableStateFlow(false)
    val isApplyingVoucher: StateFlow<Boolean> = _isApplyingVoucher.asStateFlow()

    var showSuccessDialog = mutableStateOf(false)

    var showErrorDialog =  mutableStateOf(false)

    init {
        fetchCustomerAndSetLocation()
        fetchDiscountCodes()
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

    fun completeDraftOrder(){

        viewModelScope.launch {
            val draftOrderId = sharedPreferencesHelper.getCartDraftOrderId().toString()
            val result = completeDraftOrder(draftOrderId)

            if (result) {
                toggleSuccessAlert()
            } else {
                toggleErrorAlert()
            }
        }
    }

    fun toggleSuccessAlert(){
        showSuccessDialog.value = !showSuccessDialog.value
    }
    
    fun toggleErrorAlert(){
        showErrorDialog.value = !showErrorDialog.value
    }

    fun updateVoucherText(text: String) {
        _voucherText.value = text
        _voucherError.value = null // Clear error when user types
    }

    fun applyVoucher() {
        viewModelScope.launch {
            _isApplyingVoucher.value = true
            _voucherError.value = null

            val voucherCode = _voucherText.value.trim()
            if (voucherCode.isEmpty()) {
                _voucherError.value = "Please enter a voucher code"
                _isApplyingVoucher.value = false
                return@launch
            }

            // Check if voucher exists in discount codes list
            val isValidVoucher = _discountCodes.value.any { discountCodes ->
                discountCodes.codeDiscountNodes.any { node ->
                    node.codeDiscount?.title?.equals(voucherCode, ignoreCase = true) == true
                }
            }

            if (!isValidVoucher) {
                _voucherError.value = "This voucher is not valid"
                _isApplyingVoucher.value = false
                return@launch
            }

            try {
                val draftOrderId = sharedPreferencesHelper.getCartDraftOrderId().toString()
                val updatedOrder = draftOrderUseCase.updateDraftOrderApplyVoucher(
                    draftOrderId,
                    listOf(voucherCode)
                )

                // Update applied discount amount
                val discountAmount = updatedOrder.totalDiscountsSet?.toDoubleOrNull() ?: 0.0
                _appliedDiscount.value = discountAmount

                Log.d("CheckoutViewModel", "Voucher applied successfully: $discountAmount discount")
            } catch (e: Exception) {
                _voucherError.value = "Failed to apply voucher. Please try again."
                Log.e("CheckoutViewModel", "Error applying voucher", e)
            } finally {
                _isApplyingVoucher.value = false
            }
        }
    }

    private fun fetchDiscountCodes() {
        viewModelScope.launch {
            try {
                discountCodesUseCase().collect { codes ->
                    _discountCodes.value = codes
                }
            } catch (e: Exception) {
                Log.e("DiscountCodesViewModel", "Error fetching discount codes", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
//        _selectedLocation.value = null
//        _selectedAddress.value = null
//        _discountCodes.value = emptyList()
        _voucherText.value = ""
        _voucherError.value = null
        _appliedDiscount.value = 0.0
        _isApplyingVoucher.value = false
//        showSuccessDialog.value = false
//        showErrorDialog.value = false
    }
}
