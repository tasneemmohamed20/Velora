package com.example.m_commerce.data.datasource.remote

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.http.HttpHeader

object ApolloHelper {
    private const val BASE_URL = "https://and2-ism-mad45.myshopify.com/api/2025-04/graphql.json"
    private val headers =
        mutableListOf(
            HttpHeader(
                "X-Shopify-Storefront-Access-Token",
                "da2a10babb2984a38271fe2d887ed128",
            ),
            HttpHeader(
                "Content-Type",
                "application/json",
            ),
        )

    val shopifyService: ApolloClient = ApolloClient.Builder().httpHeaders(headers).serverUrl(BASE_URL).build()

}