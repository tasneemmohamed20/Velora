package com.example.m_commerce.domain.entities

data class Customer(
    val displayName: String,
    val email: String,
    val firstName: String,
    val id: String,
    val lastName: String,
    val addresses: List<CustomerAddresses>?
)

data class CustomerAddresses(
    val address1: String?,
    val address2: String?,
    val formatted: String?
)