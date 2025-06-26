package com.example.m_commerce.data.repository_imp.customer_repo

import android.util.Log
import com.example.m_commerce.data.datasource.remote.graphql.customer.ICustomerRemoteDataSource
import com.example.m_commerce.domain.entities.Customer
import com.example.m_commerce.domain.entities.CustomerAddresses
import com.example.m_commerce.domain.repository.ICustomerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CustomerRepositoryImp @Inject constructor(private val customerRemote: ICustomerRemoteDataSource): ICustomerRepository {
    override suspend fun getCustomerIdByID(id: String): Flow<Customer> {
        return customerRemote.getCustomerIdByID(id)
    }

    override fun getCustomerIdByEmail(email: String): Flow<Customer> {
        Log.i("customer", "getCustomerIdByEmail:REPOSITORY $email")
        return customerRemote.getCustomerByEmail(email)
    }

    override suspend fun updateCustomerData(
        id: String?,
        addresses: List<CustomerAddresses>
    ): Flow<Customer> {
        return customerRemote.updateCustomerData(id, addresses)
    }
}