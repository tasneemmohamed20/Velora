package com.example.m_commerce.data.datasource.remote.graphql.order

import com.example.m_commerce.domain.entities.Order
import kotlinx.coroutines.flow.Flow

interface IOrderRemoteDataSource {
    fun getOrdersByCustomerId(customerId: String): Flow<List<Order>>
    fun getOrderById(): Flow<Order>
}