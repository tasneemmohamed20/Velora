package com.example.m_commerce.di

import com.example.m_commerce.data.datasource.remote.graphql.customer.CustomerRemoteDataSourceImp
import com.example.m_commerce.data.datasource.remote.graphql.customer.ICustomerRemoteDataSource
import com.example.m_commerce.data.repository_imp.customer_repo.CustomerRepositoryImp
import com.example.m_commerce.domain.repository.ICustomerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CustomerRepoModule {

    @Binds
    @Singleton
    abstract fun bindICustomerRepository(impl: CustomerRepositoryImp): ICustomerRepository


    @Binds
    @Singleton
    abstract fun bindICustomerRemoteDataSource(impl: CustomerRemoteDataSourceImp): ICustomerRemoteDataSource
}