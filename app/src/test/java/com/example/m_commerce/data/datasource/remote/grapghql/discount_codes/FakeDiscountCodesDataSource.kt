package com.example.m_commerce.data.datasource.remote.graphql.discount_codes

import com.example.m_commerce.data.datasource.remote.graphql.discount_codes.IDiscountCodesRemoteDataSource
import com.example.m_commerce.domain.entities.CodeDiscountNode
import com.example.m_commerce.domain.entities.DiscountCodeBasic
import com.example.m_commerce.domain.entities.DiscountCodes
import com.example.m_commerce.domain.entities.DiscountCustomerAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeDiscountCodesDataSource(
    private val discountCodes: List<DiscountCodes> = emptyList()
) : IDiscountCodesRemoteDataSource {

    override suspend fun getDiscountCodes(): Flow<List<DiscountCodes>> {
        return if (discountCodes.isNotEmpty()) {
            flowOf(discountCodes)
        } else {
            flowOf(getDefaultDiscountCodes())
        }
    }

    private fun getDefaultDiscountCodes(): List<DiscountCodes> {
        return listOf(
            DiscountCodes(
                codeDiscountNodes = listOf(
                    CodeDiscountNode(
                        id = "discount_1",
                        codeDiscount = DiscountCodeBasic(
                            customerSelection = DiscountCustomerAll(allCustomers = true),
                            summary = "10% off all items",
                            title = "SAVE10"
                        )
                    ),
                    CodeDiscountNode(
                        id = "discount_2",
                        codeDiscount = DiscountCodeBasic(
                            customerSelection = DiscountCustomerAll(allCustomers = true),
                            summary = "Free shipping on orders over $50",
                            title = "FREESHIP50"
                        )
                    ),
                    CodeDiscountNode(
                        id = "discount_3",
                        codeDiscount = DiscountCodeBasic(
                            customerSelection = DiscountCustomerAll(allCustomers = false),
                            summary = "25% off for premium members",
                            title = "PREMIUM25"
                        )
                    )
                )
            )
        )
    }
}
