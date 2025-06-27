package com.example.m_commerce.data.datasource.remote.graphql.customer

import com.example.m_commerce.domain.entities.Customer
import com.example.m_commerce.domain.entities.CustomerAddresses
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeCustomerDataSource(
    private val customers: List<Customer> = emptyList()
) : ICustomerRemoteDataSource {

    override suspend fun getCustomerIdByID(id: String): Flow<Customer> {
        val customer = customers.find { it.id == id } ?: Customer(
            displayName = "Tasneem Mohamed",
            email = "Tasneem.Mohamed@example.com",
            firstName = "Tasneem",
            id = id,
            lastName = "Mohamed",
            addresses = listOf(
                CustomerAddresses(
                    id = "address_id",
                    address1 = "P. Sherman",
                    address2 = "42 Wallaby st",
                    city = "Sydney",
                    phone = "555-1234"
                )
            )
        )
        return flowOf(customer)
    }

    override fun getCustomerByEmail(email: String): Flow<Customer> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCustomerData(
        id: String?,
        addresses: List<CustomerAddresses>?
    ): Flow<Customer> {
        val existingCustomer = customers.find { it.id == id }
        val updatedCustomer = existingCustomer?.copy(addresses = addresses) ?: Customer(
            displayName = "Updated User",
            email = "updated@example.com",
            firstName = "Updated",
            id = id ?: "updated_id",
            lastName = "User",
            addresses = addresses
        )
        return flowOf(updatedCustomer)
    }
}
