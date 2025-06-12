package com.example.m_commerce.data.datasource.remote.graphql.product

import com.apollographql.apollo.ApolloClient
import com.example.m_commerce.di.AdminClient
import com.example.m_commerce.domain.entities.Order
import com.example.m_commerce.domain.entities.Product
import com.example.m_commerce.service1.GetOrdersByCustomerQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OrderRemoteDataSourceImp @Inject constructor(@AdminClient private val shopifyService: ApolloClient) : IOrderRemoteDataSource {


    override fun getOrdersByCustomerId(customerId: String): Flow<List<Order>> = flow{
        val id = "customer_id:$customerId"
        val response = withContext(Dispatchers.IO){
            shopifyService.query(GetOrdersByCustomerQuery(customerId)).execute()
        }
        val orders = response.data?.orders?.edges?.map {
            it.node
        }?.map {
            Order(
                id = it.id,
                name = it.name,
                totalPrice = it.totalPriceSet.shopMoney.amount.toString(),
                createdAt = it.createdAt.toString(),
                financialStatus = it.displayFinancialStatus.toString(),
                fulfillmentStatus = it.displayFulfillmentStatus.toString(),
                currency = it.totalPriceSet.shopMoney.currencyCode.toString(),
            )
        } ?: emptyList()
        emit(orders)
    }



    override fun getOrderById(): Flow<Order> {
        TODO("Not yet implemented")
    }

}