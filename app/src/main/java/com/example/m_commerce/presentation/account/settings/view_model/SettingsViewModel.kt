package com.example.m_commerce.presentation.account.settings.view_model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import com.example.m_commerce.domain.usecases.CurrencyExchangeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val currencyExchangeUseCase: CurrencyExchangeUseCase,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : ViewModel() {

    private val _currencyExchange = MutableSharedFlow<CurrencyExchangeResponse>()
    val currencyExchange: SharedFlow<CurrencyExchangeResponse> = _currencyExchange
    var showLogOutDialog =  mutableStateOf(false)

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error
    
    fun getCurrencyExchange() {
        viewModelScope.launch {
            try {
                currencyExchangeUseCase().collect { response ->
                    _currencyExchange.emit(response)
                    saveUsdToEgpValue(response.rates.EGP.toFloat())
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun setCurrencyPreference(isUSD: Boolean) {
        sharedPreferencesHelper.isUSD(isUSD)
    }

    fun getCurrencyPreference(): Boolean {
        return sharedPreferencesHelper.getCurrencyPreference()
    }

    fun saveUsdToEgpValue(value: Float){
        sharedPreferencesHelper.saveUsdToEgpValue(value)
    }

    fun clearAll(){
        sharedPreferencesHelper.clearAll()
    }

    fun getCurrentUserMode(): String {
        return sharedPreferencesHelper.getCurrentUserMode()
    }
}
