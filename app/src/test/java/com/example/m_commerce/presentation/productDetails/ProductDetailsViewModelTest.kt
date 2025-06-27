package com.example.m_commerce.presentation.productDetails

import com.example.m_commerce.domain.usecases.GetProductByIdUseCase
import com.example.m_commerce.presentation.utils.ResponseState
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.DraftOrder
import com.example.m_commerce.domain.entities.DraftOrderLineItemConnection
import com.example.m_commerce.domain.usecases.DraftOrderUseCase
import com.example.m_commerce.domain.entities.Item
import com.example.m_commerce.domain.entities.LineItem
import com.example.m_commerce.domain.entities.Product
import com.example.m_commerce.domain.entities.note
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailsViewModelTest {

    private lateinit var getProductByIdUseCase: GetProductByIdUseCase
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var draftOrderUseCase: DraftOrderUseCase
    private lateinit var viewModel: ProductDetailsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getProductByIdUseCase = mockk()
        sharedPreferencesHelper = mockk(relaxed = true)
        draftOrderUseCase = mockk(relaxed = true)

        viewModel = ProductDetailsViewModel(
            getProductByIdUseCase,
            sharedPreferencesHelper,
            draftOrderUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProduct emits Success when use case returns product`() = runTest {
        // Arrange
        val productId = "123"
        val product = mockk<Product>()
        coEvery { getProductByIdUseCase(productId) } returns product

        // Act
        viewModel.loadProduct(productId)
        advanceUntilIdle()

        // Assert
        assertTrue("Expected Success but got ${viewModel.productState.value}",
            viewModel.productState.value is ResponseState.Success
        )
    }

}
