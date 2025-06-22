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

    const val ADMIN_URL = "https://and2-ism-mad45.myshopify.com/admin/api/2025-04/graphql.json"

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

    const val PUBLIC_KEY = "egy_pk_test_mId2mNbmLcV0eDl084zpeSXN0rKbHCXH"
    const val API_KEY = "ZXlKaGJHY2lPaUpJVXpVeE1pSXNJblI1Y0NJNklrcFhWQ0o5LmV5SmpiR0Z6Y3lJNklrMWxjbU5vWVc1MElpd2ljSEp2Wm1sc1pWOXdheUk2TVRBMU5ETTBOQ3dpYm1GdFpTSTZJbWx1YVhScFlXd2lmUS4zRkItZ1VXR1NSejRjVEhRX3doQjROZC1jWnZnSWk5QlFzQVI2dkQyaGlMeWl4cFNLeDVnN205R3dPdGpTaFZUQnBjVUhfazZKLTJXbVNLLVVROHU5UQ=="
    const val BASE_URL = "https://accept.paymob.com/"
    const val SECRET_KEY = "egy_sk_test_be9b78ea19f669da9912822cc24f18688762814086bdd3af02ffe4f1da2c7958"
    const val ONLINE_CARD_PAYMENT_METHOD_ID = "5147442"

}