package com.example.m_commerce.data.datasource.remote.restful.geocoding_address

import kotlinx.coroutines.flow.Flow

interface IGeocodingAddressRemoteDataSource {
    suspend fun getAddressFromGeocoding(
        latlng: String,
        apiKey: String
    ): Flow<String>
}