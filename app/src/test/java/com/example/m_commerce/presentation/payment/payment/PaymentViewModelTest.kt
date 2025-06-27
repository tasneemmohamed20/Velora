package com.example.m_commerce.presentation.payment.payment

import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.payment.*
import com.example.m_commerce.domain.usecases.CompleteDraftOrder
import com.example.m_commerce.domain.usecases.PaymentUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentViewModelTest {

    private lateinit var paymentUseCase: PaymentUseCase
    private lateinit var completeDraftOrder: CompleteDraftOrder
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var paymentViewModel: PaymentViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val mockUser = User(
        id = 1,
        username = "test",
        firstName = "Test",
        lastName = "User",
        email = "test@example.com",
        isActive = true
    )
    private val mockProfile = Profile(
        id = 1,
        user = mockUser,
        createdAt = "2024-01-01",
        active = true,
        profileType = "test",
        phones = listOf("1234567890"),
        companyEmails = listOf("company@test.com"),
        companyName = "Test Company",
        country = "EG",
        city = "Cairo"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        paymentUseCase = mockk(relaxed = true)
        completeDraftOrder = mockk(relaxed = true)
        sharedPreferencesHelper = mockk(relaxed = true)
        paymentViewModel = PaymentViewModel(paymentUseCase, completeDraftOrder, sharedPreferencesHelper)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test get auth token success`() = runTest {
        // Given
        val expectedAuthResponse = AuthResponse(profile = mockProfile, token = "test_token")
        coEvery { paymentUseCase() } returns flowOf(expectedAuthResponse)

        // When
        paymentViewModel =
            PaymentViewModel(paymentUseCase, completeDraftOrder, sharedPreferencesHelper)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(expectedAuthResponse, paymentViewModel.authState.value)
    }

    @Test
    fun `test create order success`() = runTest {
        // Given
        val authResponse = AuthResponse(profile = mockProfile, token = "test_token")
        val orderItems = listOf(
            OrderItem(
                name = "Test Item",
                amountCents = 1000.0,
                description = "Test Description",
                quantity = 1,
                itemId = "1"
            )
        )
        val expectedOrderResponse = OrderResponse(
            id = 123,
            amountCents = 1000,
            currency = "EGP",
            merchantOrderId = "123456"
        )
        val expectedPaymentKeyResponse = PaymentKeyResponse(token = "payment_token")

        coEvery { paymentUseCase() } returns flowOf(authResponse)
        coEvery {
        paymentUseCase(
                authToken = any(),
                amountCents = any(),
                items = any()
            )
        } returns flowOf(expectedOrderResponse)
        coEvery {
        paymentUseCase(
                authToken = any(),
                orderId = any(),
                amountCents = any(),
                billingData = any()
            )
        } returns flowOf(expectedPaymentKeyResponse)

        // When
        paymentViewModel =
            PaymentViewModel(paymentUseCase, completeDraftOrder, sharedPreferencesHelper)
        paymentViewModel.createOrder(1000, orderItems)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(expectedOrderResponse, paymentViewModel.orderState.value)
        assertEquals(expectedPaymentKeyResponse, paymentViewModel.paymentKeyState.value)
    }

    @Test
    fun `test complete draft order success`() = runTest {
        // Given
        val draftOrderId = "12345"
        every { sharedPreferencesHelper.getCartDraftOrderId() } returns draftOrderId
        coEvery { completeDraftOrder(draftOrderId) } returns true

        // When
        paymentViewModel =
            PaymentViewModel(paymentUseCase, completeDraftOrder, sharedPreferencesHelper)
        paymentViewModel.completeDraftOrder()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(paymentViewModel.showSuccessDialog.value)
    }


//    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `completeDraftOrder should show success dialog when completeDraftOrder returns true`() = runTest {
        // Arrange
        every { sharedPreferencesHelper.getCartDraftOrderId() } returns "12345"
        coEvery { completeDraftOrder("12345") } returns true

        // Act
        paymentViewModel.completeDraftOrder()
        advanceUntilIdle()

        // Assert
        Assert.assertTrue(paymentViewModel.showSuccessDialog.value)
    }

//    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `completeDraftOrder should show err dialog when completeDraftOrder returns false`() = runTest {
        // Arrange
        every { sharedPreferencesHelper.getCartDraftOrderId() } returns "12345"
        coEvery { completeDraftOrder("12345") } returns false

        // Act
        paymentViewModel.completeDraftOrder()
        advanceUntilIdle()

        // Assert
        assertFalse(paymentViewModel.showSuccessDialog.value)
    }
}
