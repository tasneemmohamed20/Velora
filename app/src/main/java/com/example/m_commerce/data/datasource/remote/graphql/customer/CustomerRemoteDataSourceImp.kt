package com.example.m_commerce.data.datasource.remote.graphql.customer

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.m_commerce.di.AdminApollo
import com.example.m_commerce.domain.entities.Customer
import com.example.m_commerce.domain.entities.CustomerAddresses
import com.example.m_commerce.service1.CustomerUpdateMutation
import com.example.m_commerce.service1.GetCustomerByEmailQuery
import com.example.m_commerce.service1.GetCustomerDataQuery
import com.example.m_commerce.service1.type.CustomerInput
import com.example.m_commerce.service1.type.MailingAddressInput
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
                        phone = address.phone,
                        city = address.city,
                        id = address.id
                    )
                }
            )
        } ?: Customer("", "", "", "", "", null)

        emit(customer)
    }

    override suspend fun updateCustomerData(
        id: String?,
        addresses: List<CustomerAddresses>?
    ): Flow<Customer> = flow {
        val response = withContext(Dispatchers.IO) {
            shopifyService.mutation(CustomerUpdateMutation(
                input = CustomerInput(
                    id = Optional.present(id),
                    addresses = Optional.presentIfNotNull(addresses?.map { address ->
                        MailingAddressInput(
                            address1 = Optional.presentIfNotNull(address.address1),
                            address2 = Optional.presentIfNotNull(address.address2),
                            city = Optional.presentIfNotNull(address.city),
                            phone = Optional.presentIfNotNull(address.phone)
                        )
                    })
                )
            )).execute()
        }

        val customer = response.data?.customerUpdate?.customer?.let { customerData ->
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
                        phone = address.phone,
                        city = address.city,
                        id = address.id
                    )
                }
            )
        } ?: Customer("", "", "", "", "", null)

        emit(customer)
    }


    override fun getCustomerByEmail(email: String): Flow<Customer> = flow {

        Log.i("customer", "getCustomerIdByEmail: REMOTE $email")
        val response = shopifyService.query(GetCustomerByEmailQuery(email)).execute()
        val customerNode = response.data?.customers?.edges?.firstOrNull()?.node

        val customer = customerNode?.let {
            Customer(
                displayName = it.firstName + " " + it.lastName,
                email = it.email ?: "",
                firstName = it.firstName ?: "",
                id = it.id,
                lastName = it.lastName ?: "",
                addresses = emptyList() // Not fetched in query, fill if needed later
            )
        } ?: Customer("", "", "", "", "", null)

        Log.i("customer", "getCustomerIdByEmail: $customer")
        emit(customer)
    }
}