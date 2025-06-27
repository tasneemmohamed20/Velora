package com.example.m_commerce.domain.usecases

import com.example.m_commerce.domain.entities.payment.AuthResponse
import com.example.m_commerce.domain.entities.payment.BillingData
import com.example.m_commerce.domain.entities.payment.OrderItem
import com.example.m_commerce.domain.entities.payment.OrderResponse
import com.example.m_commerce.domain.entities.payment.PaymentKeyResponse
import com.example.m_commerce.domain.repository.IPaymobRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PaymentUseCase @Inject constructor(
    private val repository: IPaymobRepository
) {
    suspend operator fun invoke(): Flow<AuthResponse> =
        repository.getAuthenticationToken()

    suspend operator fun invoke(
        authToken: String,
        amountCents: Int,
        items: List<OrderItem>
    ): Flow<OrderResponse> = repository.createOrder(
        authToken = authToken,
        amountCents = amountCents,
        items = items
    )

    suspend operator fun invoke(
        authToken: String,
        orderId: String,
        amountCents: Int,
        billingData: BillingData
    ): Flow<PaymentKeyResponse> = repository.getPaymentKey(
        authToken = authToken,
        orderId = orderId,
        amountCents = amountCents,
        billingData = billingData
    )
}