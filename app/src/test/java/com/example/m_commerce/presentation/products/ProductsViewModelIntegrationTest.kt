package com.example.m_commerce.presentation.products

import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.example.m_commerce.presentation.utils.ResponseState
import com.example.m_commerce.data.datasource.remote.grapghql.product.FakeProductRemoteDataSource
import com.example.m_commerce.data.repository_imp.products_repo.ProductsRepositoryImp
import com.example.m_commerce.domain.entities.Product
import com.example.m_commerce.domain.usecases.GetCurrencyPrefUseCase
import com.example.m_commerce.domain.usecases.GetProductsByTypeUseCase
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test


@MediumTest
class ProductsViewModelIntegrationTest {

    private lateinit var viewModel: ProductsViewModel



    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        val repo = ProductsRepositoryImp(FakeProductRemoteDataSource())
        val useCase = GetProductsByTypeUseCase(repo)
        val currencyUseCase = GetCurrencyPrefUseCase(mockk(relaxed = true))

        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = ProductsViewModel(useCase, currencyUseCase)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun teardown() = Dispatchers.resetMain()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getProductsByType_Shoes_ReturnsListOfProducts() = runTest {

        // Arrange

        // Act
        viewModel.getProductsByType("shoes")

        advanceUntilIdle()

        // Assert
        viewModel.productsList.test {
            val result = awaitItem()
            assertTrue(result is ResponseState.Success)
            assertEquals(2, ((result as ResponseState.Success).data as List<Product>).size)
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getProductsByType_None_ReturnsEmptyList() = runTest {

        // Arrange

        // Act
        viewModel.getProductsByType("")

        advanceUntilIdle()

        // Assert
        viewModel.productsList.test {
            val result = awaitItem()
            assertTrue(result is ResponseState.Success)
            assertEquals(0, ((result as ResponseState.Success).data as List<Product>).size)
        }
    }
}