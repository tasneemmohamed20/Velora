package com.example.m_commerce.di


import com.example.m_commerce.data.repository_imp.settings_repo.CurrencyExchangeRepositoryImp
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
    abstract fun bindICurrencyExchangeRepository(currentRepo: CurrencyExchangeRepositoryImp): ICurrencyExchangeRepository
}
