package com.example.m_commerce.data.datasource.remote.restful.remote

import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import com.example.m_commerce.domain.entities.payment.AuthResponse
import com.example.m_commerce.domain.entities.payment.BillingData
import com.example.m_commerce.domain.entities.payment.OrderItem
import com.example.m_commerce.domain.entities.payment.OrderResponse
import com.example.m_commerce.domain.entities.payment.PaymentKeyResponse
import kotlinx.coroutines.flow.Flow

interface RemoteDataSourceContract {
    suspend fun getCurrencyExchangeRate(apiKey: String): Flow<CurrencyExchangeResponse>

    suspend fun getAddressFromGeocoding(
        latlng: String,
        apiKey: String
    ): Flow<String>

    suspend fun getAuthenticationToken(): Flow<AuthResponse>

    suspend fun createOrder(
        authToken: String,
        amountCents: Int,
        items: List<OrderItem>
    ): Flow<OrderResponse>

    suspend fun getPaymentKey(
        authToken: String,
        orderId: String,
        amountCents: Int,
        billingData: BillingData
    ): Flow<PaymentKeyResponse>
}