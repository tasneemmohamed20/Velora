package com.example.m_commerce.data.datasource.remote.graphql.customer

import com.example.m_commerce.domain.entities.Customer
import kotlinx.coroutines.flow.Flow

interface ICustomerRemoteDataSource {
    suspend fun getCustomerIdByID(id: String): Flow<Customer>
}