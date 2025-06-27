package com.example.m_commerce.data.repository_imp.payment_repo

import com.example.m_commerce.data.datasource.remote.restful.payment.FakePaymentDataSource
import com.example.m_commerce.domain.entities.payment.BillingData
import com.example.m_commerce.domain.entities.payment.OrderItem
import com.example.m_commerce.domain.repository.IPaymobRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PaymobRepositoryImpTest {

    private lateinit var repository: IPaymobRepository
    private lateinit var fakeDataSource: FakePaymentDataSource

    @Before
    fun setUp() {
        fakeDataSource = FakePaymentDataSource()
        repository = PaymobRepositoryImp(fakeDataSource)
    }

    @Test
    fun `getAuthenticationToken should return auth response from data source`() = runTest {
        val result = repository.getAuthenticationToken().first()

        assertEquals("fake_auth_token_123", result.token)
        assertEquals(123, result.profile.id)
        assertEquals("test_user", result.profile.user.username)
    }

    @Test
    fun `createOrder should return order response from data source`() = runTest {
        val authToken = "test_token"
        val amountCents = 10000
        val items = listOf(
            OrderItem(
                name = "Test Item",
                amountCents = 5000.0,
                description = "Test",
                quantity = 2,
                itemId = "test_item_1"
            )
        )

        val result = repository.createOrder(authToken, amountCents, items).first()

        assertEquals(456L, result.id)
        assertEquals(amountCents, result.amountCents)
        assertEquals("EGP", result.currency)
        assertEquals("fake_merchant_order_id", result.merchantOrderId)
    }

    @Test
    fun `getPaymentKey should return payment key response from data source`() = runTest {
        val authToken = "test_token"
        val orderId = "test_order_id"
        val amountCents = 10000
        val billingData = BillingData(
            apartment = "123",
            email = "test@example.com",
            floor = "1",
            firstName = "John",
            street = "Test Street",
            building = "456",
            phoneNumber = "+201234567890",
            shippingMethod = "delivery",
            postalCode = "12345",
            city = "Cairo",
            country = "EG",
            lastName = "Doe",
            state = "Cairo"
        )

        val result = repository.getPaymentKey(authToken, orderId, amountCents, billingData).first()

        assertEquals("fake_payment_key_789", result.token)
    }
}
