package com.example.m_commerce.presentation.order

import app.cash.turbine.test
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.repository.IOrderRepository
import com.example.m_commerce.presentation.order.orders_list.OrderViewModel
import com.example.m_commerce.presentation.utils.ResponseState
import io.mockk.coEvery
import io.mockk.every
import org.junit.Before
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class OrderViewModelTest {


    private lateinit var viewModel: OrderViewModel
    private lateinit var repo: IOrderRepository
    private lateinit var sharedPref: SharedPreferencesHelper
    
    @Before
    fun setup() {
        sharedPref = mockk(relaxed = true)
        repo = mockk(relaxed = true)
        viewModel = OrderViewModel(
            repo,
            sharedPreferencesHelper = sharedPref
        )
    }

    @Test
    fun `getOrdersByCustomer should emit empty orders list from IOrderRepository`() = runTest {

        // Arrange
        every { sharedPref.getCustomerId() } returns "12345"
        coEvery { repo.getOrdersByCustomerId("12345") } returns flowOf(emptyList())

        // Act
         viewModel.getOrdersByCustomer()

        // Assert
        viewModel.ordersList.test {
            val emitted = awaitItem()
            assertEquals(ResponseState.Success(emptyList<Int>()), emitted)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getOrdersByCustomer should emit failure from IOrderRepository`() = runTest {

        // Arrange
        every { sharedPref.getCustomerId() } returns "12345"
        coEvery { repo.getOrdersByCustomerId("12345") } returns flow {
            throw RuntimeException("Network error")
        }

        // Act
        viewModel.getOrdersByCustomer()

        // Assert
        viewModel.ordersList.test {
            val emitted = awaitItem()
            assert(emitted is ResponseState.Failure)
            cancelAndIgnoreRemainingEvents()
        }
    }
}