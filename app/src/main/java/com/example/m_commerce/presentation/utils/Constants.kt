package com.example.m_commerce.presentation.utils

import com.apollographql.apollo.api.http.HttpHeader
import com.example.m_commerce.BuildConfig

object Constants {
    const val STOREFRONT_URL = BuildConfig.STOREFRONT_URL
    val storeHeaders = listOf(
        HttpHeader(
            "X-Shopify-Storefront-Access-Token",
            BuildConfig.STOREFRONT_ACCESS_TOKEN
        ),
        HttpHeader(
            "Content-Type",
            "application/json"
        )
    )

    const val ADMIN_URL = BuildConfig.ADMIN_URL
    val adminHeaders = listOf(
        HttpHeader(
            "X-Shopify-Access-Token",
            BuildConfig.ADMIN_ACCESS_TOKEN
        ),
        HttpHeader(
            "Content-Type",
            "application/json"
        )
    )

    const val PUBLIC_KEY = BuildConfig.PUBLIC_KEY
    const val API_KEY = BuildConfig.API_KEY
    const val BASE_URL = BuildConfig.BASE_URL
    const val SECRET_KEY = BuildConfig.SECRET_KEY
    const val ONLINE_CARD_PAYMENT_METHOD_ID = BuildConfig.ONLINE_CARD_PAYMENT_METHOD_ID

    const val MAPS_API_KEY = BuildConfig.MAPS_API_KEY
    const val WEB_CLIENT_ID = BuildConfig.WEB_CLIENT_ID
    const val CURRENCY_KEY = BuildConfig.CURRENCY_KEY
}