package com.example.m_commerce.data.datasource.remote.restful.geocoding_address

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeGeocodingAddressDataSource : IGeocodingAddressRemoteDataSource {
    override suspend fun getAddressFromGeocoding(
        latlng: String,
        apiKey: String
    ): Flow<String> {
        return when {
            latlng.contains("40.7128") -> flowOf("123 Broadway, New York, NY 10001")
            latlng.contains("34.0522") -> flowOf("456 Hollywood Blvd, Los Angeles, CA 90028")
            latlng.contains("30.0444") -> flowOf("789 Cairo Street, Cairo, Egypt")
            else -> flowOf("123 Mock Street, Mock City, Mock State 12345")
        }
    }
}
