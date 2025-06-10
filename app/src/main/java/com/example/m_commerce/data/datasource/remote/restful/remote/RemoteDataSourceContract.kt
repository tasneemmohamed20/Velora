package com.example.m_commerce.data.datasource.remote.restful.remote

import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import kotlinx.coroutines.flow.Flow

interface RemoteDataSourceContract {
    suspend fun getCurrencyExchangeRate(apiKey: String): Flow<CurrencyExchangeResponse>
    suspend fun getAddressFromGeocoding(
        latlng: String,
        apiKey: String
    ): Flow<String>
}