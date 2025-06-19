package com.example.m_commerce.data.datasource.remote.graphql.order

import com.apollographql.apollo.ApolloClient
import com.example.m_commerce.di.AdminApollo
import com.example.m_commerce.domain.entities.OrderEntity
import com.example.m_commerce.domain.entities.OrderCreateResponse
import com.example.m_commerce.domain.entities.Price
import com.example.m_commerce.domain.entities.PriceDetails
import com.example.m_commerce.domain.entities.Product
import com.example.m_commerce.domain.entities.UserError
import com.example.m_commerce.service1.CompleteDraftOrderMutation
import com.example.m_commerce.service1.GetOrdersByCustomerQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OrderRemoteDataSourceImp @Inject constructor(@AdminApollo private val shopifyService: ApolloClient) :
    IOrderRemoteDataSource {


    override fun getOrdersByCustomerId(customerId: String): Flow<List<OrderEntity>> = flow{
        val id = "customer_id:$customerId"
        val response = withContext(Dispatchers.IO){
            shopifyService.query(GetOrdersByCustomerQuery(customerId)).execute()
        }
        val orders = response.data?.orders?.edges?.map {
            it.node
        }?.map { it ->
            OrderEntity(
                id = it.id,
                name = it.name,
                totalPrice = it.totalPriceSet.shopMoney.amount.toString(),
                createdAt = it.createdAt.toString(),
                financialStatus = it.displayFinancialStatus.toString(),
                fulfillmentStatus = it.displayFulfillmentStatus.toString(),
                currency = it.totalPriceSet.shopMoney.currencyCode.toString(),
                phoneNumber = it.shippingAddress?.phone.toString(),
                address = it.shippingAddress?.address1.toString(),
                lineItems = it.lineItems.edges.map{it.node}.map{
                    Product(
                        title = it.title,
                        id = it.variant?.id ?: "",
                        productType = it.variant?.product?.productType ?: "",
                        description =  "",
                        price = PriceDetails(Price(
                            amount = it.variant?.price.toString(),
                            currencyCode = "EGP"
                        )) ,
                        images = listOf(it.variant?.product?.images?.edges?.get(0)?.node?.url.toString()),
                        quantity = it.quantity,
                    )
                },
            )
        } ?: emptyList()
        emit(orders)
    }

    override fun getOrderById(): Flow<OrderEntity> {
        TODO("Not yet implemented")
    }

    override fun completeDraftOrder(draftOrderId: String): Flow<OrderCreateResponse> = flow {
        val response = withContext(Dispatchers.IO) {
            shopifyService.mutation(
                CompleteDraftOrderMutation(id = draftOrderId)
            ).execute()
        }

        val draft = response.data?.draftOrderComplete?.draftOrder
        val userErrors = response.data?.draftOrderComplete?.userErrors?.map {
            UserError(
                field = it.field,
                message = it.message
            )
        } ?: emptyList()

        val completedOrder = draft?.let {
            OrderEntity(
                id = it.id,
                name = it.name,
                totalPrice = "",
                createdAt = "",
                financialStatus = "",
                fulfillmentStatus = "",
                currency = "",
                lineItems = emptyList()
            )
        }

        emit(OrderCreateResponse(order = completedOrder, userErrors = userErrors))
    }

}