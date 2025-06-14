package com.example.m_commerce.data.datasource.remote.grapghql.product

import com.example.m_commerce.data.datasource.remote.graphql.product.IProductRemoteDataSource
import com.example.m_commerce.domain.entities.Brand
import com.example.m_commerce.domain.entities.Price
import com.example.m_commerce.domain.entities.PriceDetails
import com.example.m_commerce.domain.entities.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeProductRemoteDataSource : IProductRemoteDataSource {

    override fun getProductsByHandle(handle: String): Flow<List<Product>> {

        return if(handle == "shoes") flowOf(
            listOf(
                Product("1", "Nike Air", "Shoes", "Sporty", PriceDetails(Price("100", "USD")), listOf("img1")),
                Product("2", "Adidas Pro", "Shoes", "Pro feel", PriceDetails(Price("120", "USD")), listOf("img2"))
            )
        ) else flowOf(emptyList())
    }

    override fun getBrands(): Flow<List<Brand>> {
        TODO("Not yet implemented")
    }

    override fun getAllProducts(): Flow<List<Product>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductById(productId: String): Product {
        TODO("Not yet implemented")
    }
}