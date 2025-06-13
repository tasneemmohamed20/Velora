package com.example.m_commerce.domain.repository

import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import kotlinx.coroutines.flow.Flow

interface ICurrencyExchangeRepository {
    suspend fun getCurrencyExchangeRate(): Flow<CurrencyExchangeResponse>
    suspend fun getCurrencyPreference(): Boolean
    suspend fun getUsdToEgp(): Float
}