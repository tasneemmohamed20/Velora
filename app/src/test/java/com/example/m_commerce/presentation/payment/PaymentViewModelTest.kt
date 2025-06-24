package com.example.m_commerce.presentation.payment

import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.usecases.CompleteDraftOrder
import com.example.m_commerce.domain.usecases.DraftOrderUseCase
import com.example.m_commerce.domain.usecases.PaymentUseCase
import com.example.m_commerce.presentation.checkout.CheckoutViewModel
import com.example.m_commerce.presentation.payment.payment.PaymentViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PaymentViewModelTest {


    private lateinit var paymentUseCase: PaymentUseCase
    private lateinit var completeDraftOrder: CompleteDraftOrder
    private lateinit var draftOrderDelete: DraftOrderUseCase
    private lateinit var sharedPref: SharedPreferencesHelper


    private lateinit var viewModel: PaymentViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        sharedPref = mockk(relaxed = true)
        paymentUseCase = mockk(relaxed = true)
        draftOrderDelete = mockk(relaxed = true)
        completeDraftOrder = mockk(relaxed = true)

        viewModel =
            PaymentViewModel(
                paymentUseCase,
                completeDraftOrder,
                draftOrderDelete,
                sharedPref
            )
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun teardown() = Dispatchers.resetMain()


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `completeDraftOrder should show success dialog when completeDraftOrder returns true`() = runTest {
        // Arrange
        every { sharedPref.getCartDraftOrderId() } returns "12345"
        coEvery { completeDraftOrder("12345") } returns true

        // Act
        viewModel.completeDraftOrder()
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.showSuccessDialog.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `completeDraftOrder should show err dialog when completeDraftOrder returns false`() = runTest {
        // Arrange
        every { sharedPref.getCartDraftOrderId() } returns "12345"
        coEvery { completeDraftOrder("12345") } returns false

        // Act
        viewModel.completeDraftOrder()
        advanceUntilIdle()

        // Assert
        assertFalse(viewModel.showSuccessDialog.value)
    }
}