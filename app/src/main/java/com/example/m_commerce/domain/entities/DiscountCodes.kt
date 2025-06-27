package com.example.m_commerce.domain.entities


data class DiscountCodes(
    val codeDiscountNodes: List<CodeDiscountNode>
)

data class CodeDiscountNode(
    val id: String,
    val codeDiscount: DiscountCodeBasic?
)

data class DiscountCodeBasic(
    val customerSelection: DiscountCustomerSelection,
    val summary: String,
    val title: String
)

sealed class DiscountCustomerSelection

data class DiscountCustomerAll(
    val allCustomers: Boolean
) : DiscountCustomerSelection()