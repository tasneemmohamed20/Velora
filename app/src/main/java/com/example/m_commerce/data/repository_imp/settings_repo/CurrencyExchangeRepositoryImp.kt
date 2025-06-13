package com.example.m_commerce.data.repository_imp.settings_repo

import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import com.example.m_commerce.data.datasource.remote.restful.remote.RemoteDataSourceContract
import com.example.m_commerce.domain.repository.ICurrencyExchangeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CurrencyExchangeRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSourceContract,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : ICurrencyExchangeRepository {

    private val currencyKey = "602456e1708f4ae9b5958fc4dbe404e6"

    override suspend fun getCurrencyExchangeRate(): Flow<CurrencyExchangeResponse> {
        return remoteDataSource.getCurrencyExchangeRate(currencyKey)
    }

    override suspend fun getCurrencyPreference(): Boolean = withContext(Dispatchers.IO) {
            sharedPreferencesHelper.getCurrencyPreference()
    }

    override suspend fun getUsdToEgp(): Float = withContext(Dispatchers.IO) {
        sharedPreferencesHelper.getUsdToEgpValue()
    }
}