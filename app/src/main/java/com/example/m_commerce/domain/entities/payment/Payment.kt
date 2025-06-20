package com.example.m_commerce.domain.entities.payment
import com.google.gson.annotations.SerializedName

data class PaymentKeyRequest(
    @SerializedName("auth_token")
    val authToken: String,
    @SerializedName("amount_cents")
    val amountCents: Int,
    @SerializedName("expiration")
    val expiration: Int = 360000,
    @SerializedName("order_id")
    val orderId: String,
    @SerializedName("billing_data")
    val billingData: BillingData,
    @SerializedName("currency")
    val currency: String = "EGP",
    @SerializedName("integration_id")
    val integrationId: Int = 5147442
)

data class BillingData(
    @SerializedName("apartment")
    val apartment: String = "NA",
    @SerializedName("email")
    val email: String,
    @SerializedName("floor")
    val floor: String = "NA",
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("street")
    val street: String,
    @SerializedName("building")
    val building: String = "NA",
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("shipping_method")
    val shippingMethod: String = "NA",
    @SerializedName("postal_code")
    val postalCode: String = "NA",
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String = "EG",
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("state")
    val state: String = "NA"
)

data class PaymentKeyResponse(
    @SerializedName("token")
    val token: String
)