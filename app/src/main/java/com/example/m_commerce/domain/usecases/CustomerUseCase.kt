package com.example.m_commerce.domain.usecases

import com.example.m_commerce.domain.repository.ICustomerRepository
import javax.inject.Inject

class CustomerUseCase @Inject constructor(private val repository: ICustomerRepository) {
    suspend operator fun invoke(id: String) = repository.getCustomerIdByID(id)
}