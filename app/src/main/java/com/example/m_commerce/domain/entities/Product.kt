package com.example.m_commerce.domain.entities


data class Product(
    val id: String,
    val title: String,
    val productType: String,
    val description: String,
    val price: PriceDetails,
    val images: List<String>,
    val variants: List<ProductVariant> = emptyList(),
    val rating: Float = 0f,
    val numberOfReviews: Int = 0

)

data class PriceDetails(
    val minVariantPrice: Price
)

data class Price(
    val amount: String,
    val currencyCode: String
)

data class ProductVariant(
    val id: String,
    val title: String,
    val availableForSale: Boolean,
    val selectedOptions: List<SelectedOption>
)

data class SelectedOption(
    val name: String,
    val value: String
)