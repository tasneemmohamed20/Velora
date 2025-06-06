package com.example.m_commerce.data.remote_data_source

import android.util.Log
import com.example.m_commerce.data.models.CurrencyExchangeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RemoteDataSourceImp : RemoteDataSourceContract {
    private val apiServices: ApiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)

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