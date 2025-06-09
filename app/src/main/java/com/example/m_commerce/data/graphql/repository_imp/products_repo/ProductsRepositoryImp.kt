package com.example.m_commerce.data.graphql.repository_imp.products_repo

import com.example.m_commerce.data.graphql.data_source.remote.product.IProductRemoteDataSource
import com.example.m_commerce.domain.entities.Brand
import com.example.m_commerce.domain.entities.Product
import com.example.m_commerce.domain.repository.IProductsRepository
import kotlinx.coroutines.flow.Flow

class ProductsRepositoryImp(private val productRemote: IProductRemoteDataSource) : IProductsRepository {
    override suspend fun getProductsByHandle(handle: String): Flow<List<Product>> {
        return productRemote.getProductsByHandle(handle)
    }

    override suspend fun getBrands(): Flow<List<Brand>> {
        return productRemote.getBrands()
    }
}