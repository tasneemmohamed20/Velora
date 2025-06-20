package com.example.m_commerce.domain.entities.payment

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("api_key")
    val apiKey: String
)

data class AuthResponse(
    @SerializedName("profile")
    val profile: Profile,
    @SerializedName("token")
    val token: String
)

data class Profile(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user")
    val user: User,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("profile_type")
    val profileType: String,
    @SerializedName("phones")
    val phones: List<String>,
    @SerializedName("company_emails")
    val companyEmails: List<String>,
    @SerializedName("company_name")
    val companyName: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("city")
    val city: String
)

data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("is_active")
    val isActive: Boolean
)