package com.example.m_commerce.di

import com.example.m_commerce.data.repository_imp.payment.PaymobRepositoryImp
import com.example.m_commerce.domain.repository.IPaymobRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class PaymobRepoModule {
    @Binds
    @Singleton
    abstract fun bingPaymentRepo(impl: PaymobRepositoryImp): IPaymobRepository
}