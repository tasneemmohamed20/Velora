package com.example.m_commerce.data.datasource.remote.product

import com.example.m_commerce.domain.entities.Brand
import kotlinx.coroutines.flow.Flow

interface IProductRemoteDataSource {
    suspend fun getBrands(): Flow<List<Brand>>
}