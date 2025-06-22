package com.example.m_commerce.domain.entities.payment
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class OrderRequest(
    @SerializedName("auth_token")
    val authToken: String,
    @SerializedName("delivery_needed")
    val deliveryNeeded: Boolean = false,
    @SerializedName("amount_cents")
    val amountCents: Int,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("items")
    val items: List<OrderItem>
)

@Serializable
data class OrderItem(
    @SerializedName("name")
    val name: String,
    @SerializedName("amount_cents")
    val amountCents: Double,
    @SerializedName("description")
    val description: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("item_id")
    val itemId: String
)

data class OrderResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("amount_cents")
    val amountCents: Int,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("merchant_order_id")
    val merchantOrderId: String
)