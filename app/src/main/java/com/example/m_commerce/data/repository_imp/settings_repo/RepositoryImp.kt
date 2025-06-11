package com.example.m_commerce.data.repository_imp.settings_repo

import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import com.example.m_commerce.data.datasource.remote.restful.remote.RemoteDataSourceContract
import com.example.m_commerce.domain.repository.ICurrencyExchangeRepository
import kotlinx.coroutines.flow.Flow

class RepositoryImp(
    private val remoteDataSource: RemoteDataSourceContract
) : ICurrencyExchangeRepository {

    private val currencyKey = "602456e1708f4ae9b5958fc4dbe404e6"

    override suspend fun getCurrencyExchangeRate(): Flow<CurrencyExchangeResponse> {
        return remoteDataSource.getCurrencyExchangeRate(currencyKey)
    }
}