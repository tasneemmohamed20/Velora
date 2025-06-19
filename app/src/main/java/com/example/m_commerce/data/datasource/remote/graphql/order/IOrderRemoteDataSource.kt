package com.example.m_commerce.data.datasource.remote.graphql.order

import com.example.m_commerce.domain.entities.OrderEntity
import com.example.m_commerce.domain.entities.OrderCreateResponse
import kotlinx.coroutines.flow.Flow

interface IOrderRemoteDataSource {
    fun getOrdersByCustomerId(customerId: String): Flow<List<OrderEntity>>
    fun getOrderById(): Flow<OrderEntity>
    fun completeDraftOrder(draftOrderId: String): Flow<OrderCreateResponse>
}