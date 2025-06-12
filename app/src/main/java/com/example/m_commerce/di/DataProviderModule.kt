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
import com.example.m_commerce.data.datasource.remote.graphql.ApolloHelper


@Module
@InstallIn(SingletonComponent::class)
object DataProviderModule {

    @Provides
    fun provideGeoRetrofit(): Retrofit {
        return RetrofitClient.getRetrofit()
    }

    @Provides
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient {
        return PlacesClientHelper.getPlacesClient(context)
    }

    @Provides
    @Singleton
    @StoreApollo
    fun provideStoreApolloClient(): ApolloClient {
        return ApolloHelper.storeApolloClient()
    }

    @Provides
    @Singleton
    @AdminApollo
    fun provideAdminApolloClient(): ApolloClient {
        return ApolloHelper.adminApolloClient()
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesHelper(@ApplicationContext context: Context): SharedPreferencesHelper {
        return SharedPreferencesHelper(context)
    }


}