package com.example.m_commerce.data.datasource.remote.restful.payment

import com.example.m_commerce.domain.entities.payment.AuthResponse
import com.example.m_commerce.domain.entities.payment.BillingData
import com.example.m_commerce.domain.entities.payment.OrderItem
import com.example.m_commerce.domain.entities.payment.OrderResponse
import com.example.m_commerce.domain.entities.payment.PaymentKeyResponse
import com.example.m_commerce.domain.entities.payment.Profile
import com.example.m_commerce.domain.entities.payment.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakePaymentDataSource : IPaymentRemoteDataSource {
    override suspend fun getAuthenticationToken(): Flow<AuthResponse> {
        return flowOf(
            AuthResponse(
                token = "fake_auth_token_123",
                profile = Profile(
                    id = 123,
                    user = User(
                        id = 1,
                        username = "test_user",
                        firstName = "Test",
                        lastName = "User",
                        email = "test@example.com",
                        isActive = true
                    ),
                    createdAt = "2024-01-01T00:00:00Z",
                    active = true,
                    profileType = "test",
                    phones = listOf("+201234567890"),
                    companyEmails = listOf("company@example.com"),
                    companyName = "Test Company",
                    country = "Egypt",
                    city = "Cairo"
                )
            )
        )
    }

    override suspend fun createOrder(
        authToken: String,
        amountCents: Int,
        items: List<OrderItem>
    ): Flow<OrderResponse> {
        return flowOf(
            OrderResponse(
                id = 456L,
                amountCents = amountCents,
                currency = "EGP",
                merchantOrderId = "fake_merchant_order_id"
            )
        )
    }

    override suspend fun getPaymentKey(
        authToken: String,
        orderId: String,
        amountCents: Int,
        billingData: BillingData
    ): Flow<PaymentKeyResponse> {
        return flowOf(
            PaymentKeyResponse(
                token = "fake_payment_key_789"
            )
        )
    }
}
