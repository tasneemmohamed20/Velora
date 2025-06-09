package com.example.m_commerce.di

import android.content.Context
import com.example.m_commerce.data.datasource.remote.restful.PlacesClientHelper
import com.example.m_commerce.data.datasource.remote.restful.RetrofitClient

import com.google.android.libraries.places.api.net.PlacesClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class DataProviderModule {

    @Provides
    fun provideGeoRetrofit(): Retrofit {
        return RetrofitClient.getRetrofit()
    }

    @Provides
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient {
        return PlacesClientHelper.getPlacesClient(context)
    }
}