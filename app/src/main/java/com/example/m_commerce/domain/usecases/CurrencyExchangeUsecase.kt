package com.example.m_commerce.domain.usecases

import com.example.m_commerce.data.models.CurrencyExchangeResponse
import com.example.m_commerce.data.repository.RepositoryContract
import kotlinx.coroutines.flow.Flow

class CurrencyExchangeUsecase(private val repository: RepositoryContract) {
    suspend operator fun invoke(): Flow<CurrencyExchangeResponse> {
        return repository.getCurrencyExchangeRate()
    }
}