package com.example.m_commerce.di

import com.example.m_commerce.data.datasource.remote.graphql.draft_orders.DraftOrderRemoteDataSourceImp
import com.example.m_commerce.data.datasource.remote.graphql.draft_orders.IDraftOrderRemoteDataSource
import com.example.m_commerce.data.repository_imp.draft_order_repo.DraftOrderRepositoryImp
import com.example.m_commerce.domain.repository.IDraftOrderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DraftOrderRepoModule {
    @Binds
    abstract fun bindIDraftOrderRepository(impl: DraftOrderRepositoryImp): IDraftOrderRepository

    @Binds
    abstract fun bindIDraftOrderRemoteDataSource(impl: DraftOrderRemoteDataSourceImp): IDraftOrderRemoteDataSource
}