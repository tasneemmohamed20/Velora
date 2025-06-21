package com.example.m_commerce.di

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.example.m_commerce.data.datasource.remote.restful.PlacesClientHelper
import com.example.m_commerce.data.datasource.remote.restful.RetrofitClient
import com.example.m_commerce.presentation.utils.Constants

import com.google.android.libraries.places.api.net.PlacesClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper



@Module
@InstallIn(SingletonComponent::class)
object DataProviderModule {

    @Provides
    @Singleton
    fun provideGeoRetrofit(): Retrofit {
        return RetrofitClient.getRetrofit()
    }

    @Provides
    @Singleton
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient {
        return PlacesClientHelper.getPlacesClient(context)
    }

    @Provides
    @Singleton
    @StoreApollo
    fun provideStoreApolloClient(): ApolloClient {
         return ApolloClient.Builder()
            .httpHeaders(Constants.storeHeaders)
            .serverUrl(Constants.STOREFRONT_URL)
            .build()
    }

    @Provides
    @Singleton
    @AdminApollo
    fun provideAdminApolloClient(): ApolloClient {
        return ApolloClient.Builder()
            .httpHeaders(Constants.adminHeaders)
            .serverUrl(Constants.ADMIN_URL)
            .build()
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesHelper(@ApplicationContext context: Context): SharedPreferencesHelper {
        return SharedPreferencesHelper(context)
    }


}