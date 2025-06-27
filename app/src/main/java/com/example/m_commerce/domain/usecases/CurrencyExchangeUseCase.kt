package com.example.m_commerce.domain.usecases

import com.example.m_commerce.data.repository_imp.currency_repo.CurrencyExchangeRepositoryImp
import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CurrencyExchangeUseCase @Inject constructor (private val repository: CurrencyExchangeRepositoryImp) {
    suspend operator fun invoke(): Flow<CurrencyExchangeResponse> {
        return repository.getCurrencyExchangeRate()
    }
}