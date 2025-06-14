package com.example.m_commerce.data.repository_imp.products_repo

import com.example.m_commerce.data.datasource.remote.graphql.product.IProductRemoteDataSource
import com.example.m_commerce.domain.entities.Price
import com.example.m_commerce.domain.entities.PriceDetails
import com.example.m_commerce.domain.entities.Product
import com.example.m_commerce.domain.repository.IProductsRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ProductsRepositoryTest {


    private lateinit var stubRemoteDataSource: IProductRemoteDataSource
    private lateinit var repo: IProductsRepository

    @Before
    fun setup() {
        stubRemoteDataSource = mockk(relaxed = true)
        repo = ProductsRepositoryImp(stubRemoteDataSource)

        every {
            repo.getProductsByHandle("vans")
        } returns flow {

            emit(
                listOf(
                    Product(
                        id = "1",
                        title = "Vans Old Skool",
                        productType = "Shoes",
                        description = "Classic skate shoe with iconic side stripe.",
                        price = PriceDetails(
                            minVariantPrice = Price(amount = "79.99", currencyCode = "USD")
                        ),
                        images = listOf(
                            "https://example.com/images/vans_old_skool_1.jpg",
                            "https://example.com/images/vans_old_skool_2.jpg"
                        )
                    ),
                    Product(
                        id = "2",
                        title = "Vans Sk8-Hi",
                        productType = "Shoes",
                        description = "High-top skate shoe with padded collar.",
                        price = PriceDetails(
                            minVariantPrice = Price(amount = "89.99", currencyCode = "USD")
                        ),
                        images = listOf(
                            "https://example.com/images/vans_sk8_hi_1.jpg"
                        )
                    )
                )
            )
        }
    }


    @Test
    fun `getProductsByHandle should return a list of products`() = runTest{
        // Arrange
        val handle = "vans"
        var result = listOf<Product>()
        // Act
        repo.getProductsByHandle(handle).collect{
            result = it
        }

        // Assert
        assert(result.size == 2)
        assert(result[0].title == "Vans Old Skool")
        assert(result[1].title == "Vans Sk8-Hi")
    }


    @Test
    fun `getProductsByHandle should return an empty list`() = runTest{
        // Arrange
        val handle = ""
        var result = listOf<Product>()

        // Act
        repo.getProductsByHandle(handle).collect{
            result = it.toMutableList()
        }

        // Assert
        assert(result.isEmpty())
    }

}