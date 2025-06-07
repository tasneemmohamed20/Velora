package com.example.m_commerce.presentation.Account.settings.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import com.example.m_commerce.domain.usecases.CurrencyExchangeUsecase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val currencyExchangeUsecase: CurrencyExchangeUsecase
) : ViewModel() {

    private val _currencyExchange = MutableSharedFlow<CurrencyExchangeResponse>()
    val currencyExchange: SharedFlow<CurrencyExchangeResponse> = _currencyExchange

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error

    fun getCurrencyExchange() {
        viewModelScope.launch {
            try {
                currencyExchangeUsecase().collect { response ->
                    _currencyExchange.emit(response)
                    Log.d("SettingsViewModel", "Currency exchange rate: ${response.rates.EGP}")
                }
            } catch (e: Exception) {
                _error.emit(e.message ?: "Unknown error occurred")
                Log.e("SettingsViewModel", "Error fetching currency exchange rate: ${e.message}")
            }
        }
    }
}