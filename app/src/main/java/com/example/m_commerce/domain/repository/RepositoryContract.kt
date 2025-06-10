package com.example.m_commerce.domain.repository

import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import kotlinx.coroutines.flow.Flow

interface RepositoryContract {
    suspend fun getCurrencyExchangeRate(): Flow<CurrencyExchangeResponse>
}