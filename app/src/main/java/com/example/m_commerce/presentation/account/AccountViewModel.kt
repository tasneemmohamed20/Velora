package com.example.m_commerce.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.usecases.CustomerUseCase
import com.example.m_commerce.presentation.utils.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val customerUseCase: CustomerUseCase,
    sharedPreferencesHelper: SharedPreferencesHelper,
) : ViewModel() {

    private val _customerState = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val customerState: StateFlow<ResponseState> = _customerState

    init {
        val customerId : String = sharedPreferencesHelper.getCustomerId().toString()
        getCustomerData(customerId)
    }

    fun getCustomerData(id: String) {
        viewModelScope.launch {
            _customerState.value = ResponseState.Loading
            customerUseCase(id)
                .catch { e ->
                    _customerState.value = ResponseState.Failure(e)
                }
                .collect { customer ->
                    _customerState.value = ResponseState.Success(customer)
                }
        }
    }
}