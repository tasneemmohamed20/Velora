package com.example.m_commerce.domain.usecases

import com.example.m_commerce.domain.repository.IDraftOrderRepository
import javax.inject.Inject

class CompleteDraftOrder@Inject constructor(private val repository: IDraftOrderRepository)  {

    suspend operator fun invoke(id: String) = repository.completeDraftOrder(id)
}
