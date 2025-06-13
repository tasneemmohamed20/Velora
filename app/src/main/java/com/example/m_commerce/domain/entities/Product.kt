package com.example.m_commerce.domain.entities


data class Product(
    val id: String,
    val title: String,
    val productType: String,
    val description: String,
    val price: PriceDetails,
    val images: List<String>
)

data class PriceDetails(
    val minVariantPrice: Price
)

data class Price(
    val amount: String,
    val currencyCode: String
)

