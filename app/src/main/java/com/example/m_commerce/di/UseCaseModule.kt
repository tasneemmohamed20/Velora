package com.example.m_commerce.di

import com.example.m_commerce.domain.repository.IProductsRepository
import com.example.m_commerce.domain.usecases.GetProductsByTypeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetProductsByTypeUseCase(
        repository: IProductsRepository
    ): GetProductsByTypeUseCase {
        return GetProductsByTypeUseCase(repository)
    }
}
