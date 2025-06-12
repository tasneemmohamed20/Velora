package com.example.m_commerce.data.repository_imp.products_repo

import com.example.m_commerce.data.datasource.remote.graphql.product.IProductRemoteDataSource
import com.example.m_commerce.di.StoreApollo
import com.example.m_commerce.domain.entities.Brand
import com.example.m_commerce.domain.entities.Product
import com.example.m_commerce.domain.repository.IProductsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductsRepositoryImp @Inject constructor(private val productRemote: IProductRemoteDataSource) : IProductsRepository {
    override suspend fun getProductsByHandle(handle: String): Flow<List<Product>> {
        return productRemote.getProductsByHandle(handle)
    }

    override suspend fun getBrands(): Flow<List<Brand>> {
        return productRemote.getBrands()
    }

    override suspend fun getAllProducts(): Flow<List<Product>> {
        return productRemote.getAllProducts()
    }
}