package com.example.m_commerce.data.repository_imp.currency_repo

import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.data.datasource.remote.restful.currency_exchange.ICurrencyExchangeRemoteDataSource
import com.example.m_commerce.domain.entities.CurrencyExchangeResponse
import com.example.m_commerce.domain.repository.ICurrencyExchangeRepository
import com.example.m_commerce.presentation.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CurrencyExchangeRepositoryImp @Inject constructor(
    private val remoteDataSource: ICurrencyExchangeRemoteDataSource,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : ICurrencyExchangeRepository {

    override suspend fun getCurrencyExchangeRate(): Flow<CurrencyExchangeResponse> {
        return remoteDataSource.getCurrencyExchangeRate(Constants.CURRENCY_KEY)
    }

    override suspend fun getCurrencyPreference(): Boolean = withContext(Dispatchers.IO) {
        sharedPreferencesHelper.getCurrencyPreference()
    }

    override suspend fun getUsdToEgp(): Float = withContext(Dispatchers.IO) {
        sharedPreferencesHelper.getUsdToEgpValue()
    }
}