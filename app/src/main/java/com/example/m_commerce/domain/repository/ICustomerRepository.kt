package com.example.m_commerce.domain.repository

import com.example.m_commerce.domain.entities.Customer
import com.example.m_commerce.domain.entities.CustomerAddresses
import kotlinx.coroutines.flow.Flow

interface ICustomerRepository {
    suspend fun getCustomerIdByID(id: String): Flow<Customer>
    suspend fun updateCustomerData(
        id: String?,
        phone: String?,
        addresses: CustomerAddresses
    ): Flow<Customer>
}