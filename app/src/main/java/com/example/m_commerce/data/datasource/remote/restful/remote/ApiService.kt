package com.example.m_commerce.data.datasource.remote.restful.remote

import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import com.example.m_commerce.domain.entities.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {
    @GET("latest")
    suspend fun getCurrentRate(
        @Query("apikey") apiKey: String,
    ): CurrencyExchangeResponse


    @GET("maps/api/geocode/json")
    suspend fun getAddressFromGeocoding(
        @Query("latlng") latlng: String,
        @Query("key") apiKey: String
    ): GeocodingResponse
}