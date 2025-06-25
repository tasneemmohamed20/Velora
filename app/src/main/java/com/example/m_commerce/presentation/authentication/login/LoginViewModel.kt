package com.example.m_commerce.presentation.authentication.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.presentation.utils.ResponseState
import com.example.m_commerce.domain.usecases.AuthUseCase
import com.example.m_commerce.domain.usecases.DraftOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val draftOrderUseCase: DraftOrderUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val loginState: StateFlow<ResponseState> = _loginState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = ResponseState.Failure(Throwable("Please enter both email and password"))
            return
        }
        _loginState.value = ResponseState.Loading

        viewModelScope.launch {
            val result = authUseCase.login(email, password)
            _loginState.value = result.fold(
                onSuccess = {
                    sharedPreferencesHelper.saveCustomerEmail(email)
                    val customerEmail = sharedPreferencesHelper.getCustomerEmail()
                    if (customerEmail != null) {
                        try {
                            var hasExistingOrder = false
                            draftOrderUseCase(customerEmail)?.collect { draftOrders ->
                                draftOrders.find { it.note2 == "cart" }?.let { draftOrder ->
                                    hasExistingOrder = true
                                    sharedPreferencesHelper.saveCartDraftOrderId(draftOrder.id.toString())
                                }
                            }
                            if (hasExistingOrder) {
                                Log.d("LoginViewModel", "Draft order already exists for customer: $customerEmail")
                            } else {
                                Log.d("LoginViewModel", "No existing draft order found for customer: $customerEmail")
                            }
                        } catch (e: Exception) {
                            Log.e("LoginViewModel", "Error checking draft order", e)
                        }
                    }
                    ResponseState.Success("Welcome back! You have successfully logged in") },
                onFailure = { ResponseState.Failure(it) }
            )
        }
    }
}