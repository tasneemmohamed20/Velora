package com.example.m_commerce.di

import com.example.m_commerce.data.datasource.remote.restful.payment.IPaymentRemoteDataSource
import com.example.m_commerce.data.datasource.remote.restful.payment.PaymentRemoteDataSourceImp
import com.example.m_commerce.data.repository_imp.payment_repo.PaymobRepositoryImp
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
    abstract fun bindPaymobRemoteDataSource(impl: PaymentRemoteDataSourceImp): IPaymentRemoteDataSource

    @Binds
    @Singleton
    abstract fun bingPaymentRepo(impl: PaymobRepositoryImp): IPaymobRepository
}