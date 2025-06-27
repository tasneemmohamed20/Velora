package com.example.m_commerce.domain.repository

import com.example.m_commerce.domain.entities.OrderEntity
import kotlinx.coroutines.flow.Flow

interface IOrderRepository {
    fun getOrdersByCustomerId(customerId: String): Flow<List<OrderEntity>>
}