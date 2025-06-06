package com.example.m_commerce.domain.repo

import com.example.m_commerce.domain.entities.Brand
import kotlinx.coroutines.flow.Flow

interface IProductsRepository {
    suspend fun getBrands(): Flow<List<Brand>>
}