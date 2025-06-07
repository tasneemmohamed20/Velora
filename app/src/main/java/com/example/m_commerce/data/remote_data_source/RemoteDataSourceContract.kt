package com.example.m_commerce.data.remote_data_source

import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import kotlinx.coroutines.flow.Flow

interface RemoteDataSourceContract {
    suspend fun getCurrencyExchangeRate(apiKey: String): Flow<CurrencyExchangeResponse>
}