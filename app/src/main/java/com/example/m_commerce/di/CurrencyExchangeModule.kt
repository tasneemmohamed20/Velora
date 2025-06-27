package com.example.m_commerce.di


import com.example.m_commerce.data.datasource.remote.restful.currency_exchange.CurrencyExchangeRemoteDataSourceImp
import com.example.m_commerce.data.datasource.remote.restful.currency_exchange.ICurrencyExchangeRemoteDataSource
import com.example.m_commerce.data.repository_imp.currency_repo.CurrencyExchangeRepositoryImp
import com.example.m_commerce.domain.repository.ICurrencyExchangeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CurrencyExchangeModule {

    @Binds
    @Singleton
    abstract fun bindCurrencyExchangeDataSource(impl: CurrencyExchangeRemoteDataSourceImp): ICurrencyExchangeRemoteDataSource


    @Binds
    @Singleton
    abstract fun bindICurrencyExchangeRepository(currentRepo: CurrencyExchangeRepositoryImp): ICurrencyExchangeRepository
}
