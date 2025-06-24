package com.example.m_commerce.data.repository_imp.order_repo

import com.example.m_commerce.data.datasource.remote.graphql.order.IOrderRemoteDataSource
import com.example.m_commerce.data.datasource.remote.graphql.product.IProductRemoteDataSource
import com.example.m_commerce.data.repository_imp.products_repo.ProductsRepositoryImp
import com.example.m_commerce.domain.entities.OrderEntity
import com.example.m_commerce.domain.entities.Price
import com.example.m_commerce.domain.entities.PriceDetails
import com.example.m_commerce.domain.entities.Product
import com.example.m_commerce.domain.repository.IOrderRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class OrderRepositoryTest {

    private lateinit var stubRemoteDataSource: IOrderRemoteDataSource
    private lateinit var repo: IOrderRepository


    @Before
    fun setup() {
        stubRemoteDataSource = mockk(relaxed = true)
        repo = OrderRepositoryImp(stubRemoteDataSource)

        every {
            repo.getOrdersByCustomerId("12345")
        } returns flow {

            emit(
                listOf(
                    OrderEntity(
                        id = "1",
                        name = "#1006",
                        totalPrice = "1000",
                        createdAt = "2025-6-10",
                        financialStatus = "Paid",
                        fulfillmentStatus = "Pending",
                        phoneNumber = "012546",
                        address = "Zagazig",
                        lineItems = listOf(Product(
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
                        )),
                        currency = "EGP"
                    )
                )
            )
        }
    }

    @Test
    fun `getOrdersByCustomerId should return a list of orders`() = runTest{
        // Arrange
        var result = listOf<OrderEntity>()
        // Act
        repo.getOrdersByCustomerId("12345").collect{
            result = it
        }

        // Assert
        assert(result.size == 1)
        assert(result[0].name == "#1006")
        assert(result[0].address == "Zagazig")
    }


    @Test
    fun `getOrdersByCustomerId should return an empty list`() = runTest{
        // Arrange

        var result = listOf<OrderEntity>()

        // Act
        repo.getOrdersByCustomerId("").collect{
            result = it
        }

        // Assert
        assert(result.isEmpty())

    }
}