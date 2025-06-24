package com.example.m_commerce.data.datasource.remote.restful.payment

import android.util.Log
import com.example.m_commerce.data.datasource.remote.restful.PaymentApiServices
import com.example.m_commerce.di.PaymentApiService
import com.example.m_commerce.domain.entities.payment.AuthRequest
import com.example.m_commerce.domain.entities.payment.AuthResponse
import com.example.m_commerce.domain.entities.payment.BillingData
import com.example.m_commerce.domain.entities.payment.OrderItem
import com.example.m_commerce.domain.entities.payment.OrderRequest
import com.example.m_commerce.domain.entities.payment.OrderResponse
import com.example.m_commerce.domain.entities.payment.PaymentKeyRequest
import com.example.m_commerce.domain.entities.payment.PaymentKeyResponse
import com.example.m_commerce.presentation.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PaymentRemoteDataSourceImp @Inject constructor(
    @PaymentApiService private val paymentApi: PaymentApiServices
) : IPaymentRemoteDataSource {

    override suspend fun getAuthenticationToken(): Flow<AuthResponse> = flow {
        try {
            val authRequest = AuthRequest(apiKey = Constants.API_KEY)
            val response = paymentApi.getAuthToken(authRequest)
            emit(response)
        } catch (e: Exception) {
            Log.e("RemoteDataSource", "Authentication Error: ${e.message}", e)
            throw e
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun createOrder(
        authToken: String,
        amountCents: Int,
        items: List<OrderItem>
    ): Flow<OrderResponse> = flow {
        try {
            val request = OrderRequest(
                authToken = authToken,
                amountCents = amountCents,
                currency = "EGP",
                items = items
            )
            val response = paymentApi.createOrder(request)
            emit(response)
        } catch (e: Exception) {
            Log.e("RemoteDataSource", "Create Order Error: ${e.message}", e)
            throw e
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getPaymentKey(
        authToken: String,
        orderId: String,
        amountCents: Int,
        billingData: BillingData
    ): Flow<PaymentKeyResponse> = flow {
        try {
            val request = PaymentKeyRequest(
                authToken = authToken,
                orderId = orderId,
                amountCents = amountCents,
                billingData = billingData,
                currency = "EGP", // handle this later using shared preferences => save current used currency
                integrationId = Constants.ONLINE_CARD_PAYMENT_METHOD_ID.toInt(),
                expiration = 360000
            )
            val response = paymentApi.getPaymentKey(request)
            emit(response)
        } catch (e: Exception) {
            Log.e("RemoteDataSource", "Payment Key Error: ${e.message}", e)
            throw e
        }
    }.flowOn(Dispatchers.IO)
}