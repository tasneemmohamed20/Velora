package com.example.m_commerce.data.datasource.remote.graphql.customer

import com.example.m_commerce.domain.entities.Customer
import com.example.m_commerce.domain.entities.CustomerAddresses
import kotlinx.coroutines.flow.Flow

interface ICustomerRemoteDataSource {
    suspend fun getCustomerIdByID(id: String): Flow<Customer>
    fun getCustomerByEmail(email: String): Flow<Customer>
    suspend fun updateCustomerData(id: String?, addresses: List<CustomerAddresses>?): Flow<Customer>
}