package com.example.m_commerce.data.datasource.remote.restful.currency_exchange

import android.util.Log
import com.example.m_commerce.data.datasource.remote.restful.CurrencyApiServices
import com.example.m_commerce.di.CurrencyApiService
import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CurrencyExchangeRemoteDataSourceImp @Inject constructor(
    @CurrencyApiService private val apiServices: CurrencyApiServices

) : ICurrencyExchangeRemoteDataSource {
    override suspend fun getCurrencyExchangeRate(apiKey: String): Flow<CurrencyExchangeResponse> {
        val response = apiServices.getCurrentRate(apiKey)
        return flow {
            emit(response)
        }.flowOn(Dispatchers.IO)
            .catch { error ->
                Log.e("RemoteDataSourceImp", "getCurrencyExchangeRate: ${error.message}")
            }

    }
}