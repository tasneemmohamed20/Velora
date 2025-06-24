package com.example.m_commerce.data.repository_imp.discout_codes_repo

import com.example.m_commerce.data.datasource.remote.graphql.discount_codes.FakeDiscountCodesDataSource
import com.example.m_commerce.data.datasource.remote.graphql.discount_codes.IDiscountCodesRemoteDataSource
import com.example.m_commerce.domain.entities.CodeDiscountNode
import com.example.m_commerce.domain.entities.DiscountCodeBasic
import com.example.m_commerce.domain.entities.DiscountCodes
import com.example.m_commerce.domain.entities.DiscountCustomerAll
import com.example.m_commerce.domain.repository.IDiscountCodesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DiscountCodesRepositoryImpTest {

    private val testDiscountCodes = listOf(
        DiscountCodes(
            codeDiscountNodes = listOf(
                CodeDiscountNode(
                    id = "test_discount_1",
                    codeDiscount = DiscountCodeBasic(
                        customerSelection = DiscountCustomerAll(allCustomers = true),
                        summary = "20% off summer collection",
                        title = "SUMMER20"
                    )
                ),
                CodeDiscountNode(
                    id = "test_discount_2",
                    codeDiscount = DiscountCodeBasic(
                        customerSelection = DiscountCustomerAll(allCustomers = false),
                        summary = "Buy 2 get 1 free",
                        title = "BUY2GET1"
                    )
                )
            )
        ),
        DiscountCodes(
            codeDiscountNodes = listOf(
                CodeDiscountNode(
                    id = "test_discount_3",
                    codeDiscount = DiscountCodeBasic(
                        customerSelection = DiscountCustomerAll(allCustomers = true),
                        summary = "Student discount 15%",
                        title = "STUDENT15"
                    )
                )
            )
        )
    )

    private lateinit var fakeDiscountCodesDataSource: IDiscountCodesRemoteDataSource
    private lateinit var discountCodesRepository: IDiscountCodesRepository

    @Before
    fun setUp() {
        fakeDiscountCodesDataSource = FakeDiscountCodesDataSource(testDiscountCodes)
        discountCodesRepository = DiscountCodesRepositoryImp(fakeDiscountCodesDataSource)
    }

    @Test
    fun `getDiscountCodes should return provided discount codes list`() = runTest {
        
        // When
        val result = discountCodesRepository.getDiscountCodes().first()

        // Then
        assertEquals(2, result.size)

        // Verify first 
        assertEquals(2, result[0].codeDiscountNodes.size)
        val firstCode = result[0].codeDiscountNodes[0]
        assertEquals("test_discount_1", firstCode.id)
        assertEquals("SUMMER20", firstCode.codeDiscount?.title)
        assertEquals("20% off summer collection", firstCode.codeDiscount?.summary)

        // Verify second 
        val secondCode = result[0].codeDiscountNodes[1]
        assertEquals("test_discount_2", secondCode.id)
        assertEquals("BUY2GET1", secondCode.codeDiscount?.title)
        assertEquals("Buy 2 get 1 free", secondCode.codeDiscount?.summary)

        // Verify second 
        assertEquals(1, result[1].codeDiscountNodes.size)
        val thirdCode = result[1].codeDiscountNodes[0]
        assertEquals("test_discount_3", thirdCode.id)
        assertEquals("STUDENT15", thirdCode.codeDiscount?.title)
        assertEquals("Student discount 15%", thirdCode.codeDiscount?.summary)
    }
    
}
