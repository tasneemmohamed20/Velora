package com.example.m_commerce.di

import com.example.m_commerce.data.datasource.remote.graphql.discount_codes.DiscountCodesRemoteDataSourceImp
import com.example.m_commerce.data.datasource.remote.graphql.discount_codes.IDiscountCodesRemoteDataSource
import com.example.m_commerce.data.repository_imp.discout_codes.DiscountCodesRepositoryImp
import com.example.m_commerce.domain.repository.IDiscountCodesRepository


import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DiscountCodesModule {
    @Binds
    abstract fun bindIDiscountCodesRemoteDataSource(impl: DiscountCodesRemoteDataSourceImp): IDiscountCodesRemoteDataSource

    @Binds
    abstract fun bindIDiscountCodesRepository(impl: DiscountCodesRepositoryImp): IDiscountCodesRepository
}
