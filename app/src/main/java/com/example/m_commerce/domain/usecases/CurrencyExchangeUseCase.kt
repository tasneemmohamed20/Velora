package com.example.m_commerce.domain.usecases

import com.example.m_commerce.data.repository_imp.settings_repo.RepositoryImp
import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import kotlinx.coroutines.flow.Flow

class CurrencyExchangeUseCase(private val repository: RepositoryImp) {
    suspend operator fun invoke(): Flow<CurrencyExchangeResponse> {
        return repository.getCurrencyExchangeRate()
    }
}