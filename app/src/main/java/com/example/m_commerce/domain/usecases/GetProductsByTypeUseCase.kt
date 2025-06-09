package com.example.m_commerce.domain.usecases

import com.example.m_commerce.domain.repository.IProductsRepository

class GetProductsByTypeUseCase(private val repository: IProductsRepository) {
    suspend operator fun invoke(handle: String) = repository.getProductsByHandle(handle)
}