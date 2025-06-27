package com.example.m_commerce.data.datasource.remote.graphql.discount_codes

import com.apollographql.apollo.ApolloClient
import com.example.m_commerce.di.AdminApollo
import com.example.m_commerce.domain.entities.CodeDiscountNode
import com.example.m_commerce.domain.entities.DiscountCodeBasic
import com.example.m_commerce.domain.entities.DiscountCodes
import com.example.m_commerce.domain.entities.DiscountCustomerAll
import com.example.m_commerce.service1.CodeDiscountNodesQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DiscountCodesRemoteDataSourceImp @Inject constructor(@AdminApollo private val shopifyService: ApolloClient) : IDiscountCodesRemoteDataSource {

    override suspend fun getDiscountCodes(): Flow<List<DiscountCodes>> = flow {
        val response = shopifyService.query(CodeDiscountNodesQuery()).execute()
        val nodes = response.data?.codeDiscountNodes?.nodes?.mapNotNull { node ->
            node?.let {
                CodeDiscountNode(
                    id = it.id,
                    codeDiscount = it.codeDiscount.onDiscountCodeBasic?.let { basic ->
                        DiscountCodeBasic(
                            customerSelection = basic.customerSelection.onDiscountCustomerAll?.let { customerAll ->
                                DiscountCustomerAll(
                                    allCustomers = customerAll.allCustomers
                                )
                            } ?: DiscountCustomerAll(allCustomers = false),
                            summary = basic.summary ?: "",
                            title = basic.title ?: ""
                        )
                    }
                )
            }
        } ?: emptyList()
        emit(listOf(DiscountCodes(codeDiscountNodes = nodes)))
    }
}
