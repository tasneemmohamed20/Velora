package com.example.m_commerce.domain.repository

import com.example.m_commerce.domain.entities.Brand
import kotlinx.coroutines.flow.Flow

interface IProductsRepository {
    suspend fun getBrands(): Flow<List<Brand>>
}