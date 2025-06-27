package com.example.m_commerce.presentation.payment.checkout

import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.usecases.CompleteDraftOrder
import com.example.m_commerce.domain.usecases.CustomerUseCase
import com.example.m_commerce.domain.usecases.DiscountCodesUsecse
import com.example.m_commerce.domain.usecases.DraftOrderUseCase
import com.example.m_commerce.presentation.checkout.CheckoutViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class CheckoutViewModelTest {
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var customerUseCase: CustomerUseCase
    lateinit var draftOrderUseCase: DraftOrderUseCase
    lateinit var completeDraftOrder: CompleteDraftOrder
    lateinit var discountCodesUseCase: DiscountCodesUsecse
    private val testDispatcher = StandardTestDispatcher()
    lateinit var checkoutViewModel: CheckoutViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        sharedPreferencesHelper = mockk(relaxed = true)
        customerUseCase = mockk(relaxed = true)
        draftOrderUseCase = mockk(relaxed = true)
        completeDraftOrder = mockk(relaxed = true)
        discountCodesUseCase = mockk(relaxed = true)
        checkoutViewModel = CheckoutViewModel(sharedPreferencesHelper, customerUseCase, draftOrderUseCase, completeDraftOrder, discountCodesUseCase)
    }

    @Test
    fun `extractLatLng parses coordinates and updates selected address`() = runTest {
        val address = "lat:30.0|lon:31.0|area:Cairo"
        val (lat, lon) = checkoutViewModel.extractLatLng(address)
        assert(lat == 30.0 && lon == 31.0)
        assert(checkoutViewModel.selectedAddress.value == "Cairo")
    }


    @Test
    fun `completeDraftOrder toggles success dialog on success`() = runTest {
        every { sharedPreferencesHelper.getCartDraftOrderId() } returns 1L.toString()
        coEvery { completeDraftOrder(any()) } returns true
        checkoutViewModel.completeDraftOrder()
        testDispatcher.scheduler.advanceUntilIdle()
        assert(checkoutViewModel.showSuccessDialog.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `completeDraftOrder should show success dialog when completeDraftOrder returns true`() = runTest {
        // Arrange
        every { sharedPreferencesHelper.getCartDraftOrderId() } returns "12345"
        coEvery { completeDraftOrder("12345") } returns true

        // Act
        checkoutViewModel.completeDraftOrder()
        advanceUntilIdle()

        // Assert
        assertTrue(checkoutViewModel.showSuccessDialog.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `completeDraftOrder should show err dialog when completeDraftOrder returns false`() = runTest {
        // Arrange
        every { sharedPreferencesHelper.getCartDraftOrderId() } returns "12345"
        coEvery { completeDraftOrder("12345") } returns false

        // Act
        checkoutViewModel.completeDraftOrder()
        advanceUntilIdle()

        // Assert
        assertFalse(checkoutViewModel.showSuccessDialog.value)
    }
}