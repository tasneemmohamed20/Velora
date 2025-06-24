package com.example.m_commerce.data.datasource.remote.restful.geocoding_address

import android.util.Log
import com.example.m_commerce.data.datasource.remote.restful.GeocodingApiServices
import com.example.m_commerce.di.GeocodingApiService
import com.example.m_commerce.domain.entities.GeocodingResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GeocodingAddressRemoteDataSourceImp @Inject constructor(
    @GeocodingApiService private val geocodingApi: GeocodingApiServices
) : IGeocodingAddressRemoteDataSource {

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