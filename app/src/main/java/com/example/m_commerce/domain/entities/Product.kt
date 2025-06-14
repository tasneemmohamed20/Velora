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

enum class note {
    cart,
    fav;

    companion object {
        fun get(value: String): note? {
            return note.entries.find { it.name == value }
        }
    }
}

data class SelectedOption(
    val name: String,
    val value: String
)