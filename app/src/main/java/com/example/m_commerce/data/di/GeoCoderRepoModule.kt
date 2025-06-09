package com.example.m_commerce.data.di

import com.example.m_commerce.data.restful.data_source.remote.RemoteDataSourceContract
import com.example.m_commerce.data.restful.data_source.remote.RemoteDataSourceImp
import com.example.m_commerce.data.restful.repository_imp.GeoCodingRepositoryImp
import com.example.m_commerce.domain.repository.IGeoCodingRepository
import dagger.Binds
import dagger.Module

import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class GeoCoderRepoModule {

    @Binds
    abstract fun bindRemoteDataSource(impl: RemoteDataSourceImp): RemoteDataSourceContract

    @Binds
    @Singleton
    abstract fun bindGeoCoderRepo(impl: GeoCodingRepositoryImp): IGeoCodingRepository
}