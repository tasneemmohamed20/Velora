package com.example.m_commerce.data.datasource.remote.restful

import android.content.Context
import com.example.m_commerce.R
import com.example.m_commerce.presentation.utils.Constants
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.currencyfreaks.com/v2.0/rates/"

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}


object RetrofitGeoHelper{
    private const val BASE_URL = "https://maps.googleapis.com/"

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object PlacesClientHelper {
    fun getPlacesClient(context: Context): PlacesClient {
        if (!Places.isInitialized()) {
            Places.initialize(context, context.getString(R.string.MAPS_API_KEY))
        }
        return Places.createClient(context)
    }
}

object PaymobRetrofitHelper {
    private const val BASE_URL = Constants.BASE_URL

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

