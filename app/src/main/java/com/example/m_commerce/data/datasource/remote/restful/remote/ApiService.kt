package com.example.m_commerce.data.datasource.remote.restful.remote

import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import com.example.m_commerce.domain.entities.GeocodingResponse
import com.example.m_commerce.domain.entities.payment.AuthRequest
import com.example.m_commerce.domain.entities.payment.AuthResponse
import com.example.m_commerce.domain.entities.payment.OrderRequest
import com.example.m_commerce.domain.entities.payment.OrderResponse
import com.example.m_commerce.domain.entities.payment.PaymentKeyRequest
import com.example.m_commerce.domain.entities.payment.PaymentKeyResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
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

    @POST("api/auth/tokens")
    @Headers("Content-Type: application/json")
    suspend fun getAuthToken(
        @Body authRequest: AuthRequest
    ): AuthResponse

    @POST("api/ecommerce/orders")
    @Headers("Content-Type: application/json")
    suspend fun createOrder(
        @Body orderRequest: OrderRequest
    ): OrderResponse

    @POST("api/acceptance/payment_keys")
    @Headers("Content-Type: application/json")
    suspend fun getPaymentKey(
        @Body paymentKeyRequest: PaymentKeyRequest
    ): PaymentKeyResponse

}