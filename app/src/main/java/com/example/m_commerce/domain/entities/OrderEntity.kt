package com.example.m_commerce.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderEntity(
    val id: String,
    val name: String,
    val totalPrice: String,
    val createdAt: String,
    val financialStatus: String?,
    val fulfillmentStatus: String?,
    val phoneNumber: String = "",
    val address: String = "",
    val lineItems: List<Product>?,
    val currency: String,
) : Parcelable


