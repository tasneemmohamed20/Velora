package com.example.m_commerce.di

import com.example.m_commerce.data.datasource.remote.graphql.product.IProductRemoteDataSource
import com.example.m_commerce.data.datasource.remote.graphql.product.ProductRemoteDataSourceImp
import com.example.m_commerce.data.repository_imp.products_repo.ProductsRepositoryImp
import com.example.m_commerce.domain.repository.IProductsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class ProductsRepoModule {


    @Binds
    @Singleton
    abstract fun bindIProductRemoteDataSource(productRemoteDataSource: ProductRemoteDataSourceImp): IProductRemoteDataSource


    @Binds
    @Singleton
    abstract fun bindIProductRepository(productRepo: ProductsRepositoryImp): IProductsRepository

}