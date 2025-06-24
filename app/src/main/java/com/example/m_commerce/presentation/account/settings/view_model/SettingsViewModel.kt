package com.example.m_commerce.presentation.account.settings.view_model

import android.util.Log
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

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error

    fun getCurrencyExchange() {
        viewModelScope.launch {
            try {
                currencyExchangeUseCase().collect { response ->
                    _currencyExchange.emit(response)
                    saveUsdToEgpValue(response.rates.EGP.toFloat())
//                    Log.d("SettingsViewModel", "Currency exchange rate: ${response.rates.EGP}")
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Unknown error occurred")

//                Log.e("SettingsViewModel", "Error fetching currency exchange rate: ${e.message}")
            }
        }
    }

    fun setCurrencyPreference(isUSD: Boolean) {
        sharedPreferencesHelper.isUSD(isUSD)
        Log.d("SettingsViewModel", "Currency preference set to USD: $isUSD")
    }

    fun getCurrencyPreference(): Boolean {
        return sharedPreferencesHelper.getCurrencyPreference()
    }

    fun saveUsdToEgpValue(value: Float){
        sharedPreferencesHelper.saveUsdToEgpValue(value)
    }
}