package com.example.m_commerce.presentation.utils

import com.apollographql.apollo.api.http.HttpHeader

object Constants {

    const val STOREFRONT_URL = "https://and2-ism-mad45.myshopify.com/api/2025-04/graphql.json"
    val storeHeaders =
        listOf(
            HttpHeader(
                "X-Shopify-Storefront-Access-Token",
                "da2a10babb2984a38271fe2d887ed128",
            ),
            HttpHeader(
                "Content-Type",
                "application/json",
            ),
        )


}