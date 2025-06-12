package com.example.m_commerce.presentation.account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.Customer
import com.example.m_commerce.domain.usecases.CustomerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val customerUseCase: CustomerUseCase,
    sharedPreferencesHelper: SharedPreferencesHelper
) : ViewModel() {

    private val _customerState = MutableStateFlow<Customer?>(null)
    val customerState: StateFlow<Customer?> = _customerState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        val customerId : String = sharedPreferencesHelper.getCustomerId().toString()
        getCustomerData(customerId)
    }

    fun getCustomerData(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            customerUseCase(id)
                .catch { e ->
                    Log.e("AccountViewModel", "getCustomerData: ${e.message}")
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { customer ->
                    Log.i("AccountViewModel", "getCustomerData: $customer")
                    _customerState.value = customer
                    _isLoading.value = false
                }
        }
    }
}