package com.example.m_commerce.domain.entities

data class OrderCreateResponse(
    val order: OrderEntity?,
    val userErrors: List<UserError>
)


data class UserError(
    val field: List<String>?,
    val message: String
)
