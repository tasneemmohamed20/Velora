package com.example.m_commerce.presentation.home


import app.cash.turbine.test
import com.example.m_commerce.domain.entities.Brand
import com.example.m_commerce.domain.entities.Price
import com.example.m_commerce.domain.entities.PriceDetails
import com.example.m_commerce.domain.entities.Product
import com.example.m_commerce.domain.repository.IProductsRepository
import com.example.m_commerce.presentation.utils.ResponseState
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class HomeViewModelTest {


    private lateinit var viewModel: HomeViewModel
    private lateinit var repo: IProductsRepository

    @Before
    fun setup() {
        repo = mockk(relaxed = true)
        viewModel = HomeViewModel(repo)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getBrands should emit brand list from Repository`() = runTest {
        // Arrange

        val fakeBrands = listOf(
            Brand("1", "Nike", "Shoes"),
            Brand("2", "Adidas", "Shoes")
        )

        coEvery { repo.getBrands() } returns flowOf(fakeBrands)
        // Act
        viewModel.getBrands()

        advanceUntilIdle()
        // Assert
        viewModel.brandsList.test {
            val emitted = awaitItem()
            assertEquals(ResponseState.Success(fakeBrands), emitted)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getBrands should emit failure due to network error`() = runTest {
        // Arrange
        val exception = RuntimeException("Network error")
        coEvery { repo.getBrands() } returns flow {
            throw exception
        }

        // Act
        viewModel.getBrands()

        // Assert
        viewModel.brandsList.test {
            val emitted = awaitItem()
            assert(emitted is ResponseState.Failure)
            assert((emitted as ResponseState.Failure).err.message === "Network error")
            cancelAndIgnoreRemainingEvents()
        }
    }


}