package com.example.m_commerce.domain.repository

import com.example.m_commerce.domain.entities.Brand
import com.example.m_commerce.domain.entities.Product
import kotlinx.coroutines.flow.Flow

interface IProductsRepository {
    fun getProductsByHandle(handle: String): Flow<List<Product>>
    fun getBrands(): Flow<List<Brand>>
}