package com.example.m_commerce.data.restful.data_source.remote

import android.util.Log
import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import com.example.m_commerce.domain.entities.GeocodingResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RemoteDataSourceImp @Inject constructor(): RemoteDataSourceContract {
    private val apiServices: ApiServices = RetrofitClient.getRetrofit().create(ApiServices::class.java)
    private val geocodingApi: ApiServices = RetrofitGeoHelper.getRetrofit().create(ApiServices::class.java)

    override suspend fun getCurrencyExchangeRate(apiKey: String): Flow<CurrencyExchangeResponse> {
        val response = apiServices.getCurrentRate(apiKey)
        return flow {
            emit(response)
        }.flowOn(Dispatchers.IO)
            .catch { error ->
                Log.e("RemoteDataSourceImp", "getCurrencyExchangeRate: ${error.message}")
            }

    }

    override suspend fun getAddressFromGeocoding(
        latlng: String,
        apiKey: String
    ): Flow<String> {
        val response: GeocodingResponse = geocodingApi.getAddressFromGeocoding(latlng, apiKey)
        return flow {
            if (response.results.isNotEmpty()) {
                emit(response.results[0].formattedAddress)
            } else {
                emit("No address found")
            }
        }.flowOn(Dispatchers.IO)
            .catch { e ->
                Log.e("RemoteDataSource", "API Error: ${e.message}", e)
                throw e
            }
    }

}