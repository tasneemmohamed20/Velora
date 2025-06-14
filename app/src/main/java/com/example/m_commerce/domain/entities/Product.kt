package com.example.m_commerce.domain.entities


data class Product(
    val id: String,
    val title: String,
    val productType: String,
    val description: String,
    val price: PriceDetails,
    val images: List<String>,
    val variants: Variant
)

data class PriceDetails(
    val minVariantPrice: Price
)

data class Price(
    val amount: String,
    val currencyCode: String
)

data class Variant(
    val id: String,
)

enum class note {
    cart,
    fav;

    companion object {
        fun get(value: String): note? {
            return note.entries.find { it.name == value }
        }
    }
}
