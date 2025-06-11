package com.example.m_commerce.domain.repository

import com.example.m_commerce.domain.entities.Customer
import kotlinx.coroutines.flow.Flow

interface ICustomerRepository {
    suspend fun getCustomerIdByID(id: String): Flow<Customer>
}