package com.example.m_commerce.presentation.payment.payment

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.payment.AuthResponse
import com.example.m_commerce.domain.entities.payment.BillingData
import com.example.m_commerce.domain.entities.payment.OrderItem
import com.example.m_commerce.domain.entities.payment.OrderResponse
import com.example.m_commerce.domain.entities.payment.PaymentKeyResponse
import com.example.m_commerce.domain.usecases.CompleteDraftOrder
import com.example.m_commerce.domain.usecases.PaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentUseCase: PaymentUseCase,
    private val completeDraftOrder: CompleteDraftOrder,
    private val sharedPreferencesHelper: SharedPreferencesHelper,
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthResponse?>(null)
    val authState = _authState.asStateFlow()

    private val _orderState = MutableStateFlow<OrderResponse?>(null)
    val orderState = _orderState.asStateFlow()

    private val _paymentKeyState = MutableStateFlow<PaymentKeyResponse?>(null)
    val paymentKeyState = _paymentKeyState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    var showSuccessDialog = mutableStateOf(false)
    var showErrorDialog =  mutableStateOf(false)

    init {
        getAuthToken()
    }

    private fun getAuthToken() {
        viewModelScope.launch {
            try {
                paymentUseCase().collect { response ->
                    _authState.value = response
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Authentication failed"
            }
        }
    }

    fun createOrder(amountCents: Int, items: List<OrderItem>) {
        viewModelScope.launch {
            try {
                val authToken = _authState.value?.token
                    ?: throw IllegalStateException("Authentication token not found")

                paymentUseCase(
                    authToken = authToken,
                    amountCents = amountCents,
                    items = items
                ).collect { response ->
                    _orderState.value = response
                    getPaymentKey(response.id.toString(), amountCents)
//                    Log.d("PaymentViewModel" ,"Order created with ID: ${response.id}")
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Order creation failed"
            }
        }
    }

    private fun getPaymentKey(orderId: String, amountCents: Int) {
        viewModelScope.launch {
            try {
                val authToken = _authState.value?.token
                    ?: throw IllegalStateException("Authentication token not found")

                val billingData = BillingData(
                    email = "tasneemmohamed20@gmail.com",
                    firstName = "Test",
                    lastName = "Customer",
                    phoneNumber = "01010101010",
                    street = "Test Street",
                    city = "Test City",
                )

                paymentUseCase(
                    authToken = authToken,
                    orderId = orderId,
                    amountCents = amountCents,
                    billingData = billingData
                ).collect { response ->
                    _paymentKeyState.value = response
//                    Log.d("PaymentViewModel", "Payment key received: ${response.token}")
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Payment key generation failed"
            }
        }
    }


    fun completeDraftOrder(){
//        Log.i("completeDraftOrder", "customerId: ${sharedPreferencesHelper.getCustomerId()} ")
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
}