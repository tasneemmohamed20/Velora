package com.example.m_commerce.presentation.products

import app.cash.turbine.test
import com.example.m_commerce.ResponseState
import com.example.m_commerce.domain.entities.Price
import com.example.m_commerce.domain.entities.PriceDetails
import com.example.m_commerce.domain.entities.Product
import com.example.m_commerce.domain.usecases.GetCurrencyPrefUseCase
import com.example.m_commerce.domain.usecases.GetProductsByTypeUseCase
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class ProductsViewModelTest {


    private lateinit var viewModel: ProductsViewModel
    private lateinit var getProductsByTypeUseCase: GetProductsByTypeUseCase
    private lateinit var getCurrencyPrefUseCase: GetCurrencyPrefUseCase

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        getProductsByTypeUseCase = mockk(relaxed = true)
        getCurrencyPrefUseCase = mockk(relaxed = true)

        viewModel = ProductsViewModel(getProductsByTypeUseCase, getCurrencyPrefUseCase)

        Dispatchers.setMain(StandardTestDispatcher())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun teardown() = Dispatchers.resetMain()


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getProductsByType should emit product list from use case`() = runTest {
        // Arrange

        val fakeProducts = listOf(
            Product("1", "Nike Air", "Shoes", "Cool shoes", PriceDetails(Price("100", "USD")), listOf("img1")),
            Product("2", "Adidas Pro", "Shoes", "Sporty", PriceDetails(Price("120", "USD")), listOf("img2"))
        )

        coEvery { getProductsByTypeUseCase("shoes") } returns flowOf(fakeProducts)
        // Act
        viewModel.getProductsByType("shoes")

        advanceUntilIdle()
        // Assert
        viewModel.productsList.test {
            val emitted = awaitItem()
            assertEquals(ResponseState.Success(fakeProducts), emitted)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getCurrencyPref should emit currency preference`() = runTest {
        // Arrange
        val expectedPref = Pair(true, 47.5f)

        coEvery { getCurrencyPrefUseCase() } returns expectedPref

        // Act
        viewModel.getCurrencyPref()

        advanceUntilIdle()
        // Assert
        viewModel.currencyPrefFlow.test{
            val emitted = awaitItem()
            assertEquals(expectedPref, emitted)
            cancelAndIgnoreRemainingEvents()
        }
    }

}