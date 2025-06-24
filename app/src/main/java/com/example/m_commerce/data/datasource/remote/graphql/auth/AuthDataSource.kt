package com.example.m_commerce.data.datasource.remote.graphql.auth


interface AuthDataSource {
    suspend fun signUp(email: String, password: String, firstName: String, lastName: String): Result<Unit>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun createShopifyCustomer(email: String, firstName: String, lastName: String): Result<Unit>
    suspend fun fetchShopifyCustomerId(email: String): Result<Unit>
}