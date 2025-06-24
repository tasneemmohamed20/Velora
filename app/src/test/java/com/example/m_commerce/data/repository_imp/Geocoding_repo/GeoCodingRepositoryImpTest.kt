package com.example.m_commerce.data.repository_imp.Geocoding_repo

import com.example.m_commerce.data.datasource.remote.restful.geocoding_address.FakeGeocodingAddressDataSource
import com.example.m_commerce.domain.repository.IGeoCodingRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GeoCodingRepositoryImpTest {

    private lateinit var fakeRemoteDataSource: FakeGeocodingAddressDataSource
    private lateinit var repository: IGeoCodingRepository
    val apiKey = "test_api_key"
    @Before
    fun setUp() {
        fakeRemoteDataSource = FakeGeocodingAddressDataSource()
        repository = GeoCodingRepositoryImp(fakeRemoteDataSource)
    }

    @Test
    fun getAddressFromGeocoding_withNewYorkCoordinates_returnsNewYorkAddress() = runTest {
        // Given
        val latlng = "40.7128,-74.0060"


        // When
        val result = repository.getAddressFromGeocoding(latlng, apiKey).first()

        // Then
        assertEquals("123 Broadway, New York, NY 10001", result)
    }

    @Test
    fun getAddressFromGeocoding_withLosAngelesCoordinates_returnsLosAngelesAddress() = runTest {
        // Given
        val latlng = "34.0522,-118.2437"

        // When
        val result = repository.getAddressFromGeocoding(latlng, apiKey).first()

        // Then
        assertEquals("456 Hollywood Blvd, Los Angeles, CA 90028", result)
    }

    @Test
    fun getAddressFromGeocoding_withUnknownCoordinates_returnsDefaultMockAddress() = runTest {
        // Given
        val latlng = "25.2048,55.2708"

        // When
        val result = repository.getAddressFromGeocoding(latlng, apiKey).first()

        // Then
        assertEquals("123 Mock Street, Mock City, Mock State 12345", result)
    }
}
