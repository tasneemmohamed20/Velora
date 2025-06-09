package com.example.m_commerce.data.graphql.data_source.remote.product

import com.example.m_commerce.domain.entities.Brand
import com.example.m_commerce.domain.entities.Product
import kotlinx.coroutines.flow.Flow

interface IProductRemoteDataSource {

    suspend fun getProductsByHandle(handle: String): Flow<List<Product>>
    suspend fun getBrands(): Flow<List<Brand>>
}