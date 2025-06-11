package com.example.m_commerce.data.datasource.remote.graphql

import com.apollographql.apollo.ApolloClient
import com.example.m_commerce.presentation.utils.Constants

class ApolloHelper {
    companion object {
        fun storeApolloClient(): ApolloClient {
            return ApolloClient.Builder()
                .httpHeaders(Constants.storeHeaders)
                .serverUrl(Constants.STOREFRONT_URL)
                .build()
        }

        fun adminApolloClient(): ApolloClient {
            return ApolloClient.Builder()
                .httpHeaders(Constants.adminHeaders)
                .serverUrl(Constants.ADMIN_URL)
                .build()
        }
    }
}