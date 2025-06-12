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

    const val ADMIN_URL = "https://and2-ism-mad45.myshopify.com/admin/api/2024-10/graphql.json"

    val adminHeaders =
        listOf(
            HttpHeader(
                "X-Shopify-Access-Token",
                "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",
            ),
            HttpHeader(
                "Content-Type",
                "application/json",
            ),
        )

}