package com.example.m_commerce.domain.repository

import com.example.m_commerce.domain.entities.Order
import kotlinx.coroutines.flow.Flow

interface IOrderRepository {
    fun getOrdersByCustomerId(customerId: String): Flow<List<Order>>
}