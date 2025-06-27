package com.example.m_commerce.di

import android.content.Context
import androidx.work.WorkManager
import com.apollographql.apollo.ApolloClient
import com.example.m_commerce.data.datasource.remote.restful.CurrencyApiServices
import com.example.m_commerce.data.datasource.remote.restful.GeocodingApiServices
import com.example.m_commerce.data.datasource.remote.restful.PaymentApiServices
import com.example.m_commerce.data.datasource.remote.restful.CurrencyRetrofitClient
import com.example.m_commerce.data.datasource.remote.restful.RetrofitGeoHelper
import com.example.m_commerce.data.datasource.remote.restful.PaymobRetrofitHelper
import com.example.m_commerce.presentation.utils.Constants
import com.example.m_commerce.presentation.utils.ConnectivityHelper
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.data.datasource.remote.restful.PlacesClientHelper
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataProviderModule {

    @Provides
    @Singleton
    @GeocodingApiService
    fun provideGeoRetrofit(): GeocodingApiServices {
        return RetrofitGeoHelper.getRetrofit().create(GeocodingApiServices::class.java)
    }

    @Provides
    @Singleton
    @CurrencyApiService
    fun provideCurrencyRetrofit(): CurrencyApiServices {
        return CurrencyRetrofitClient.getRetrofit().create(CurrencyApiServices::class.java)
    }

    @Provides
    @Singleton
    @PaymentApiService
    fun providePaymobRetrofit(): PaymentApiServices {
        return PaymobRetrofitHelper.getRetrofit().create(PaymentApiServices::class.java)
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

    @Provides
    @Singleton
    fun provideConnectivityHelper(@ApplicationContext context: Context): ConnectivityHelper {
        return ConnectivityHelper(context)
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object WorkManagerModule {
        @Provides
        @Singleton
        fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
            return WorkManager.getInstance(context)
        }
    }
}
