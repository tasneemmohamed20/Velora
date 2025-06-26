package com.example.m_commerce.presentation.favorite

import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.*
import com.example.m_commerce.domain.usecases.FavoriteProductsUseCases
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteViewModelTest {

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var favoriteProductsUseCases: FavoriteProductsUseCases
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private val testDispatcher = StandardTestDispatcher()


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        favoriteProductsUseCases = mockk(relaxed = true)
        sharedPreferencesHelper = mockk(relaxed = true)
        viewModel = FavoriteViewModel(favoriteProductsUseCases, sharedPreferencesHelper)
    }

    @Test
    fun `lineItemToProduct should return non-null Product for valid LineItem`() {
        val variant = ProductVariant(
            id = "v1",
            title = "Size 42",
            availableForSale = true,
            selectedOptions = listOf(SelectedOption(name = "Size", value = "42"))
        )

        val product = Product(
            id = "p1",
            title = "Mock Product",
            productType = "Shoes",
            description = "A nice shoe",
            price = PriceDetails(
                minVariantPrice = Price("100.0", "USD")
            ),
            images = listOf("url"),
            variants = listOf(variant)
        )

        val lineItem = LineItem(
            originalUnitPrice = 100.0,
            title = "Mock Product",
            name = "Mock Product Desc",
            image = Image(url = "url"),
            product = product,
            variantId = "v1"
        )

        val result = viewModel.lineItemToProduct(lineItem)
        assertNotNull(result)
        assertEquals("p1", result?.id)
    }


    @Test
    fun `toggleProductFavorite should add and remove product from favorites`() = runTest {
        val variantId = "v1"
        val product = Product(
            id = "p1",
            title = "Mock Product",
            productType = "Shoes",
            description = "A nice shoe",
            price = PriceDetails(Price("100.0", "USD")),
            images = listOf("url"),
            variants = listOf(
                ProductVariant(
                    id = variantId,
                    title = "Size 42",
                    availableForSale = true,
                    selectedOptions = listOf(SelectedOption("Size", "42"))
                )
            )
        )

        viewModel.toggleProductFavorite(product, variantId)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.favoriteProducts.value.size)
        assertTrue(viewModel.favoriteVariantIds.value.contains(variantId))

        viewModel.toggleProductFavorite(product, variantId)

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.favoriteProducts.value.isEmpty())
        assertFalse(viewModel.favoriteVariantIds.value.contains(variantId))
    }

}
