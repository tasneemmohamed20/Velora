package com.example.m_commerce.domain.usecases

import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import com.example.m_commerce.domain.repository.RepositoryContract
import kotlinx.coroutines.flow.Flow

class CurrencyExchangeUseCase(private val repository: RepositoryContract) {
    suspend operator fun invoke(): Flow<CurrencyExchangeResponse> {
        return repository.getCurrencyExchangeRate()
    }
}