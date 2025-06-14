package com.example.m_commerce.domain.usecases

import com.example.m_commerce.domain.repository.IProductsRepository
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(
    private val repository: IProductsRepository
) {
    suspend operator fun invoke(productId: String) = repository.getProductById(productId)
}