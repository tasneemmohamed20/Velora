package com.example.m_commerce.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
data class Product(
    val id: String,
    val title: String,
    val productType: String,
    val description: String,
    val price: PriceDetails,
    val images: List<String>,
    val variants: List<ProductVariant>,
    val rating: Float = 0f,
    val quantity: Int = 0,
    val numberOfReviews: Int = 0

) : Parcelable

@Parcelize
data class PriceDetails(
    val minVariantPrice: Price
) : Parcelable

@Parcelize
data class Price(
    val amount: String,
    val currencyCode: String
) : Parcelable

@Parcelize
data class ProductVariant(
    val id: String,
    val title: String?,
    val availableForSale: Boolean?,
    val selectedOptions: List<SelectedOption?>?
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
    val title: String,
    val availableForSale: Boolean,
    val selectedOptions: List<SelectedOption>
) : Parcelable

@Parcelize
data class SelectedOption(
    val name: String,
    val value: String
) : Parcelable