package com.example.m_commerce.data.repository_imp.order_repo

import com.example.m_commerce.data.datasource.remote.graphql.order.IOrderRemoteDataSource
import com.example.m_commerce.domain.entities.OrderEntity
import com.example.m_commerce.domain.repository.IOrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OrderRepositoryImp @Inject constructor(private val orderRemoteDataSource: IOrderRemoteDataSource) : IOrderRepository {

    override fun getOrdersByCustomerId(customerId: String): Flow<List<OrderEntity>> {
        return orderRemoteDataSource.getOrdersByCustomerId(customerId)
    }
}