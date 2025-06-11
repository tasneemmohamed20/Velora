package com.example.m_commerce.data.datasource.remote.graphql.customer

import com.apollographql.apollo.ApolloClient
import com.example.m_commerce.di.AdminApollo
import com.example.m_commerce.domain.entities.Customer
import com.example.m_commerce.domain.entities.CustomerAddresses
import com.example.m_commerce.service1.GetCustomerDataQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CustomerRemoteDataSourceImp @Inject constructor(@AdminApollo private val shopifyService: ApolloClient) : ICustomerRemoteDataSource {

    override suspend fun getCustomerIdByID(id: String): Flow<Customer> = flow {
        val response = withContext(Dispatchers.IO) {
            shopifyService.query(GetCustomerDataQuery(id)).execute()
        }

        val customer = response.data?.customer?.let { customerData ->
            Customer(
                displayName = customerData.displayName,
                email = customerData.email ?: "",
                firstName = customerData.firstName ?: "",
                id = customerData.id,
                lastName = customerData.lastName ?: "",
                addresses = customerData.addresses.map { address ->
                    CustomerAddresses(
                        address1 = address.address1,
                        address2 = address.address2,
                        formatted = address.formatted.toString()
                    )
                }
            )
        } ?: Customer("", "", "", "", "", null)

        emit(customer)
    }
}