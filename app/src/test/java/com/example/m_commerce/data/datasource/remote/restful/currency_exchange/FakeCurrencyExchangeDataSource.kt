package com.example.m_commerce.data.datasource.remote.restful.currency_exchange

import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import com.example.m_commerce.domain.entities.Rates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeCurrencyExchangeDataSource : ICurrencyExchangeRemoteDataSource {
    override suspend fun getCurrencyExchangeRate(apiKey: String): Flow<CurrencyExchangeResponse> {
        val fakeResponse = CurrencyExchangeResponse(
            base = "USD",
            date = "2025-06-24",
            rates = Rates(EGP = "50.68")
        )
        return flowOf(fakeResponse)
    }
}
