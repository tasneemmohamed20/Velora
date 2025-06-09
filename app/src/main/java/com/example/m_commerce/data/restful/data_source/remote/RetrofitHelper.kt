package com.example.m_commerce.data.restful.data_source.remote

import android.content.Context
import androidx.compose.runtime.remember
import com.example.m_commerce.R
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