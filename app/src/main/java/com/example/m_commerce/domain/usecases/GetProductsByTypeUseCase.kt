package com.example.m_commerce.domain.usecases

import com.example.m_commerce.domain.repository.IProductsRepository
import javax.inject.Inject

class GetProductsByTypeUseCase @Inject constructor(private val repository: IProductsRepository) {
    operator fun invoke(handle: String) = repository.getProductsByHandle(handle)
}