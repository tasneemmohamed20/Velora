package com.example.m_commerce.data.repository

import com.example.m_commerce.data.models.CurrencyExchangeResponse
import kotlinx.coroutines.flow.Flow

interface RepositoryContract {
    suspend fun getCurrencyExchangeRate(): Flow<CurrencyExchangeResponse>
}