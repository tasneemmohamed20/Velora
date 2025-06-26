package com.example.m_commerce.presentation.search

import app.cash.turbine.test
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.*
import com.example.m_commerce.domain.usecases.GetAllProductsUseCase
import com.example.m_commerce.domain.usecases.GetCurrencyPrefUseCase
import com.example.m_commerce.presentation.utils.ResponseState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var getAllProductsUseCase: GetAllProductsUseCase
    private lateinit var currencyPrefUseCase: GetCurrencyPrefUseCase
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var viewModel: SearchViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getAllProductsUseCase = mockk()
        currencyPrefUseCase = mockk()
        sharedPreferencesHelper = mockk()

        every { sharedPreferencesHelper.getCurrencyPreference() } returns true

        val fakeProducts = listOf(
            Product(
                id = "1",
                title = "Shirt",
                productType = "Clothing",
                description = "Nice shirt",
                price = PriceDetails(Price("100.0", "USD")),
                images = listOf(),
                variants = emptyList()
            ),
            Product(
                id = "2",
                title = "Shoes",
                productType = "Footwear",
                description = "Comfy shoes",
                price = PriceDetails(Price("300.0", "USD")),
                images = listOf(),
                variants = emptyList()
            )
        )

        coEvery { getAllProductsUseCase() } returns flow {
            delay(100)
            emit(fakeProducts)
        }

        viewModel = SearchViewModel(
            getAllProductsUseCase,
            currencyPrefUseCase,
            sharedPreferencesHelper
        )
    }

    @Test
    fun `fetchProductsAndInitPrices should emit loading, then success with correct price boundaries`() = runTest {
        val job = launch {
            viewModel.productsList.test {
                assertEquals(ResponseState.Loading, awaitItem())

                val successState = awaitItem()
                assertTrue(successState is ResponseState.Success)

                val products = (successState as ResponseState.Success).data as List<Product>

                assertEquals(2, products.size)
                assertEquals("Shirt", products[0].title)
                assertEquals("Shoes", products[1].title)

                assertEquals(100.0, viewModel.minAllowedPrice.value, 0.01)
                assertEquals(300.0, viewModel.maxAllowedPrice.value, 0.01)
                assertEquals(300.0, viewModel.currentMaxPrice.value, 0.01)

                cancelAndIgnoreRemainingEvents()
            }
        }

        viewModel.fetchProductsAndInitPrices()

        job.join()
    }

    @Test
    fun `searchProducts should filter products based on title and price range`() = runTest {
        // Given
        viewModel.fetchProductsAndInitPrices()
        advanceUntilIdle()

        viewModel.onQueryChange("shirt")
        viewModel.onMaxPriceChange(150.0)

        // When
        viewModel.searchProducts()

        // Then
        viewModel.productsList.test {
            assertEquals(ResponseState.Loading, awaitItem())

            val state = awaitItem()
            assertTrue("Expected Success but got $state", state is ResponseState.Success)

            val filtered = (state as ResponseState.Success).data as List<Product>
            assertEquals(1, filtered.size)
            assertEquals("Shirt", filtered.first().title)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCurrencyCode returns USD when preference is true`() {
        // Arrange
        every { sharedPreferencesHelper.getCurrencyPreference() } returns true

        // Act
        val result = viewModel.getCurrencyCode()

        // Assert
        assertEquals("USD", result)
    }

    @Test
    fun `getCurrencyCode returns EGP when preference is false`() {
        // Arrange
        every { sharedPreferencesHelper.getCurrencyPreference() } returns false

        // Act
        val result = viewModel.getCurrencyCode()

        // Assert
        assertEquals("EGP", result)
    }


}
