package com.example.m_commerce.data.repo_imp

import com.example.m_commerce.data.datasource.remote.product.IProductRemoteDataSource
import com.example.m_commerce.domain.entities.Brand
import com.example.m_commerce.domain.repo.IProductsRepository
import kotlinx.coroutines.flow.Flow

class ProductsRepositoryImp(private val productRemote: IProductRemoteDataSource) : IProductsRepository {

    override suspend fun getBrands(): Flow<List<Brand>> {
        return productRemote.getBrands()
    }
}