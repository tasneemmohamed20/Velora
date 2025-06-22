package com.example.m_commerce.data.repository_imp.payment

import com.example.m_commerce.data.datasource.remote.restful.remote.RemoteDataSourceContract
import com.example.m_commerce.domain.entities.payment.AuthResponse
import com.example.m_commerce.domain.entities.payment.BillingData
import com.example.m_commerce.domain.entities.payment.OrderItem
import com.example.m_commerce.domain.entities.payment.OrderResponse
import com.example.m_commerce.domain.entities.payment.PaymentKeyResponse
import com.example.m_commerce.domain.repository.IPaymobRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PaymobRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSourceContract
) : IPaymobRepository {

    override suspend fun getAuthenticationToken(): Flow<AuthResponse> {
        return remoteDataSource.getAuthenticationToken()
    }

    override suspend fun createOrder(
        authToken: String,
        amountCents: Int,
        items: List<OrderItem>
    ): Flow<OrderResponse> {
        return remoteDataSource.createOrder(
            authToken = authToken,
            amountCents = amountCents,
            items = items
        )
    }

    override suspend fun getPaymentKey(
        authToken: String,
        orderId: String,
        amountCents: Int,
        billingData: BillingData
    ): Flow<PaymentKeyResponse> {
        return remoteDataSource.getPaymentKey(
            authToken = authToken,
            orderId = orderId,
            amountCents = amountCents,
            billingData = billingData
        )
    }
}