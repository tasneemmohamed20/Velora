package com.example.m_commerce.data.repository_imp.customer_repo

import com.example.m_commerce.data.datasource.remote.graphql.customer.FakeCustomerDataSource
import com.example.m_commerce.data.datasource.remote.graphql.customer.ICustomerRemoteDataSource
import com.example.m_commerce.domain.entities.Customer
import com.example.m_commerce.domain.entities.CustomerAddresses
import com.example.m_commerce.domain.repository.ICustomerRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CustomerRepositoryImpTest {

    private val testCustomers = listOf(
        Customer(
            displayName = "Alice Smith",
            email = "alice@example.com",
            firstName = "Alice",
            id = "customer_1",
            lastName = "Smith",
            addresses = listOf(
                CustomerAddresses(
                    id = "addr_1",
                    address1 = "456 Oak St",
                    address2 = "Unit 2A",
                    city = "Boston",
                    phone = "555-5678"
                ),
                CustomerAddresses(
                    id = "addr_2",
                    address1 = "789 Pine Ave",
                    address2 = null,
                    city = "Cambridge",
                    phone = "555-9012"
                )
            )
        ),
        Customer(
            displayName = "Bob Johnson",
            email = "bob@example.com",
            firstName = "Bob",
            id = "customer_2",
            lastName = "Johnson",
            addresses = listOf(
                CustomerAddresses(
                    id = "addr_3",
                    address1 = "321 Elm St",
                    address2 = "Apt 5C",
                    city = "Chicago",
                    phone = "555-3456"
                )
            )
        )
    )

    private lateinit var fakeCustomerDataSource: ICustomerRemoteDataSource
    private lateinit var customerRepository: ICustomerRepository

    @Before
    fun setUp() {
        fakeCustomerDataSource = FakeCustomerDataSource(testCustomers)
        customerRepository = CustomerRepositoryImp(fakeCustomerDataSource)
    }

    @Test
    fun `getCustomerIdByID should return existing customer when customer exists`() = runTest {
        // Given
        val customerId = "customer_1"

        // When
        val result = customerRepository.getCustomerIdByID(customerId).first()

        // Then
        assertEquals("customer_1", result.id)
        assertEquals("Alice Smith", result.displayName)
        assertEquals("alice@example.com", result.email)
        assertEquals("Alice", result.firstName)
        assertEquals("Smith", result.lastName)
        assertEquals(2, result.addresses?.size)
        assertEquals("456 Oak St", result.addresses?.get(0)?.address1)
        assertEquals("Boston", result.addresses?.get(0)?.city)
    }


    @Test
    fun `updateCustomerData should update existing customer addresses`() = runBlocking {
        // Given
        val customerId = "customer_1"
        val newAddresses = listOf(
            CustomerAddresses(
                id = "new_addr_1",
                address1 = "999 Broadway",
                address2 = "Suite 100",
                city = "San Francisco",
                phone = "555-7890"
            ),
            CustomerAddresses(
                id = "new_addr_2",
                address1 = "111 Market St",
                address2 = null,
                city = "Oakland",
                phone = "555-1111"
            )
        )

        // When
        val result = customerRepository.updateCustomerData(customerId, newAddresses).first()

        // Then
        assertEquals("customer_1", result.id)
        assertEquals("Alice Smith", result.displayName)
        assertEquals("alice@example.com", result.email)
        assertEquals(2, result.addresses?.size)
        assertEquals("999 Broadway", result.addresses?.get(0)?.address1)
        assertEquals("San Francisco", result.addresses?.get(0)?.city)
        assertEquals("111 Market St", result.addresses?.get(1)?.address1)
        assertEquals("Oakland", result.addresses?.get(1)?.city)
    }


    @Test
    fun `updateCustomerData should handle empty addresses list`() = runBlocking {
        // Given
        val customerId = "customer_2"
        val emptyAddresses = emptyList<CustomerAddresses>()

        // When
        val result = customerRepository.updateCustomerData(customerId, emptyAddresses).first()

        // Then
        assertEquals("customer_2", result.id)
        assertEquals("Bob Johnson", result.displayName)
        assertEquals("bob@example.com", result.email)
        assertTrue(result.addresses?.isEmpty() == true)
    }

}
