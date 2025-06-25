package com.example.m_commerce.presentation.main.viewmodel

import androidx.lifecycle.ViewModel
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.presentation.utils.ConnectivityHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val connectivityHelper: ConnectivityHelper,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : ViewModel() {
    val isConnected: StateFlow<Boolean> = connectivityHelper.isConnected

    private val _isLogged = MutableStateFlow(false)
    val isLogged: StateFlow<Boolean> = _isLogged

    init {
        _isLogged.value = getCustomerEmail() != null
    }

    fun getCustomerEmail(): String?{
        return sharedPreferencesHelper.getCustomerEmail()
    }
}
