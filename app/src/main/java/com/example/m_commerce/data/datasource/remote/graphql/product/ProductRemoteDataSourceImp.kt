package com.example.m_commerce.data.datasource.remote.graphql.product

import com.apollographql.apollo.ApolloClient
import com.example.m_commerce.di.StoreApollo
import com.example.m_commerce.domain.entities.Brand
import com.example.m_commerce.domain.entities.Price
import com.example.m_commerce.domain.entities.PriceDetails
import com.example.m_commerce.domain.entities.Product
import com.example.m_commerce.domain.entities.ProductVariant
import com.example.m_commerce.domain.entities.SelectedOption
import com.example.m_commerce.service2.GetAllProductsQuery
import com.example.m_commerce.service2.GetBrandsQuery
import com.example.m_commerce.service2.GetProductByIdQuery
import com.example.m_commerce.service2.GetProductsByHandleQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRemoteDataSourceImp @Inject constructor(@StoreApollo private val shopifyService: ApolloClient) : IProductRemoteDataSource {


    override fun getProductsByHandle(handle: String): Flow<List<Product>> = flow{

        val response = withContext(Dispatchers.IO){
            shopifyService.query(GetProductsByHandleQuery(handle)).execute()
        }

        val products = response.data?.collection?.products?.edges
            ?.map { it.node }
            ?.map { node ->
                Product(
                    id = node.id,
                    title = node.title,
                    productType = node.productType,
                    description = node.description,
                    price = PriceDetails(
                        minVariantPrice = Price(
                            amount = node.priceRange.minVariantPrice.amount.toString(),
                            currencyCode = node.priceRange.minVariantPrice.currencyCode.name
                        )
                    ),
                    images = node.images.edges.map { it.node.url.toString() }
                )
            } ?: emptyList()
        emit(products)
    }

    override fun getBrands(): Flow<List<Brand>> = flow {
            val response = withContext(Dispatchers.IO) {
                shopifyService.query(GetBrandsQuery()).execute()
            }
            val brands = response.data?.collections?.edges
                ?.map { it.node }
                ?.map { node ->
                    Brand(
                        id = node.id,
                        title = node.title,
                        imageUrl = node.image?.url.toString()
                    )
                } ?: emptyList()
            emit(brands)
    }

    override fun getAllProducts(): Flow<List<Product>> = flow {
        val response = withContext(Dispatchers.IO) {
            shopifyService.query(GetAllProductsQuery()).execute()
        }
        val products = response.data?.products?.edges
            ?.map { it.node }
            ?.map { node ->
                Product(
                    id = node.id,
                    title = node.title,
                    description = node.description,
                    price = PriceDetails(
                        minVariantPrice = Price(
                            amount = node.priceRange.minVariantPrice.amount.toString(),
                            currencyCode = node.priceRange.minVariantPrice.currencyCode.name
                        )
                    ),
                    images = node.images.edges.map { it.node.url.toString() },
                    productType = "",
                )
            } ?: emptyList()
        emit(products)
    }

    override suspend fun getProductById(productId: String): Product {
        val response = withContext(Dispatchers.IO) {
            shopifyService.query(GetProductByIdQuery(productId)).execute()
        }
        val node = response.data?.product ?: throw Exception("Product not found")
        return Product(
            id = node.id,
            title = node.title,
            productType = node.productType,
            description = node.description,
            price = PriceDetails(
                minVariantPrice = Price(
                    amount = node.priceRange.minVariantPrice.amount.toString(),
                    currencyCode = node.priceRange.minVariantPrice.currencyCode.name
                )
            ),
            images = node.images.edges.map { it.node.url.toString() },
            variants = node.variants.edges.map { edge ->
                ProductVariant(
                    id = edge.node.id,
                    title = edge.node.title,
                    availableForSale = edge.node.availableForSale,
                    selectedOptions = edge.node.selectedOptions.map { option ->
                        SelectedOption(
                            name = option.name,
                            value = option.value
                        )
                    }
                )
            }
        )

    }
}