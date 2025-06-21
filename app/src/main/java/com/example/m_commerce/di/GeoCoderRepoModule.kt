package com.example.m_commerce.di

import com.example.m_commerce.data.datasource.remote.restful.remote.RemoteDataSourceContract
import com.example.m_commerce.data.datasource.remote.restful.remote.RemoteDataSourceImp
import com.example.m_commerce.data.repository_imp.settings_repo.GeoCodingRepositoryImp
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
    @Singleton
    abstract fun bindRemoteDataSource(impl: RemoteDataSourceImp): RemoteDataSourceContract

    @Binds
    @Singleton
    abstract fun bindGeoCoderRepo(impl: GeoCodingRepositoryImp): IGeoCodingRepository
}