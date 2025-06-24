package com.example.m_commerce.data.datasource.remote.restful.currency_exchange

import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import kotlinx.coroutines.flow.Flow

interface ICurrencyExchangeRemoteDataSource {
    suspend fun getCurrencyExchangeRate(apiKey: String): Flow<CurrencyExchangeResponse>

}