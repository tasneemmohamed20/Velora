package com.example.m_commerce.presentation.cart

import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.DraftOrder
import com.example.m_commerce.domain.entities.DraftOrderLineItemConnection
import com.example.m_commerce.domain.usecases.DraftOrderUseCase
import com.example.m_commerce.presentation.utils.ResponseState
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {
    lateinit var draftOrderUseCase: DraftOrderUseCase
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var cartViewModel: CartViewModel
    private val testDispatcher = StandardTestDispatcher()


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        draftOrderUseCase = mockk(relaxed = true)
        sharedPreferencesHelper = mockk(relaxed = true)
        cartViewModel = CartViewModel(draftOrderUseCase, sharedPreferencesHelper)
    }

    @Test
    fun `loadCartItems sets cartState to Failure if customer email is missing`() = runTest {
        every { sharedPreferencesHelper.getCustomerEmail() } returns null
        cartViewModel.loadCartItems()
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(cartViewModel.cartState.value is ResponseState.Failure)
    }

    @Test
    fun `loadCartItems sets cartState to Success and saves draft order ID when cart found`() = runTest {
        val lineItemsConnection = DraftOrderLineItemConnection(nodes = emptyList())
        val draftOrder = DraftOrder(id = "1", note2 = "cart", lineItems = lineItemsConnection)
        every { sharedPreferencesHelper.getCustomerEmail() } returns "test@example.com"
        coEvery { draftOrderUseCase.invoke(any()) } returns flowOf(listOf(draftOrder))
        every { sharedPreferencesHelper.saveCartDraftOrderId(any()) } just Runs
        cartViewModel.loadCartItems()

        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(cartViewModel.cartState.value is ResponseState.Success)
        verify { sharedPreferencesHelper.saveCartDraftOrderId("1") }
    }

    @Test
    fun `updateQuantity updates cartState to Loading and updates item quantity`() = runTest {
        val draftOrder = mockk<DraftOrder>(relaxed = true) {
            every { lineItems?.nodes } returns listOf(
                mockk {
                    every { id } returns "variant1"
                    every { variantId } returns "variant1"
                }
            )
        }
        cartViewModel.currentOrder = draftOrder
        cartViewModel.updateQuantity("variant1", 5)
        assertEquals(ResponseState.Loading, cartViewModel.cartState.value)
    }
}