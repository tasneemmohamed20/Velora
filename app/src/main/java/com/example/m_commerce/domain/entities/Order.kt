package com.example.m_commerce.domain.entities

data class Order(
    val id: String,
    val name: String,
    val totalPrice: String,
    val createdAt: String,
    val financialStatus: String?,
    val fulfillmentStatus: String?,
    val currency: String,
)

