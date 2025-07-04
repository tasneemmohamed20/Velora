package com.example.m_commerce.di

import com.example.m_commerce.data.datasource.remote.graphql.auth.AuthDataSource
import com.example.m_commerce.data.datasource.remote.graphql.auth.AuthDataSourceImpl
import com.example.m_commerce.data.datasource.remote.graphql.favorite.FavoriteRemoteDataSourceImpl
import com.example.m_commerce.data.datasource.remote.graphql.favorite.IFavoriteRemoteDataSource
import com.example.m_commerce.data.datasource.remote.graphql.order.IOrderRemoteDataSource
import com.example.m_commerce.data.datasource.remote.graphql.product.IProductRemoteDataSource
import com.example.m_commerce.data.datasource.remote.graphql.order.OrderRemoteDataSourceImp
import com.example.m_commerce.data.datasource.remote.graphql.product.ProductRemoteDataSourceImp
import com.example.m_commerce.data.repository_imp.auth_repo.AuthRepositoryImpl
import com.example.m_commerce.data.repository_imp.favorite_repo.FavoriteRepositoryImp
import com.example.m_commerce.data.repository_imp.order_repo.OrderRepositoryImp
import com.example.m_commerce.data.repository_imp.products_repo.ProductsRepositoryImp
import com.example.m_commerce.domain.repository.IAuthRepository
import com.example.m_commerce.domain.repository.IFavoriteRepository
import com.example.m_commerce.domain.repository.IOrderRepository
import com.example.m_commerce.domain.repository.IProductsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class ReposModule {


    @Binds
    @Singleton
    abstract fun bindIProductRemoteDataSource(productRemoteDataSource: ProductRemoteDataSourceImp): IProductRemoteDataSource


    @Binds
    @Singleton
    abstract fun bindIProductRepository(productRepo: ProductsRepositoryImp): IProductsRepository



    @Binds
    @Singleton
    abstract fun bindIOrderRemoteDataSource(orderRemoteDataSource: OrderRemoteDataSourceImp): IOrderRemoteDataSource


    @Binds
    @Singleton
    abstract fun bindIOrderRepository(orderRepo: OrderRepositoryImp): IOrderRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(favoriteRepositoryImpl: FavoriteRepositoryImp): IFavoriteRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRemoteDataSource(favoriteRemoteDataSourceImpl: FavoriteRemoteDataSourceImpl): IFavoriteRemoteDataSource


    @Binds
    @Singleton
    abstract fun bindAuthDataSource(authDataSourceImpl: AuthDataSourceImpl): AuthDataSource

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): IAuthRepository
}