package com.example.m_commerce.domain.usecases


import com.example.m_commerce.domain.entities.Product
import com.example.m_commerce.domain.repository.IProductsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllProductsUseCase @Inject constructor(
    private val repository: IProductsRepository
) {
    suspend operator fun invoke(): Flow<List<Product>> {
        return repository.getAllProducts()
    }
}