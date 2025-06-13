package com.example.m_commerce.data.repository_imp.order_repo

import com.example.m_commerce.data.datasource.remote.graphql.product.IOrderRemoteDataSource
import com.example.m_commerce.domain.entities.Order
import com.example.m_commerce.domain.repository.IOrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OrderRepositoryImp @Inject constructor(private val orderRemoteDataSource: IOrderRemoteDataSource) : IOrderRepository {

    override fun getOrdersByCustomerId(customerId: String): Flow<List<Order>> {
        return orderRemoteDataSource.getOrdersByCustomerId(customerId)
    }
}