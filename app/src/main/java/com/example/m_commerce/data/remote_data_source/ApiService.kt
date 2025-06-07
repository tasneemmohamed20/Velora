package com.example.m_commerce.data.remote_data_source

import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {
    @GET("latest")
    suspend fun getCurrentRate(
        @Query("apikey") apiKey: String,
    ): CurrencyExchangeResponse
}