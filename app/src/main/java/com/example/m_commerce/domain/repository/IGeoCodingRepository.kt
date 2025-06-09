package com.example.m_commerce.domain.repository

import kotlinx.coroutines.flow.Flow

interface IGeoCodingRepository {
    suspend fun getAddressFromGeocoding(latlng: String, apiKey: String): Flow<String>
}