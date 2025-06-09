package com.example.m_commerce.domain.entities

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class GeocodingResponse(
    val results: List<GeocodingResults>,
    val status: String
)
data class GeocodingResults(
    @SerializedName("formatted_address")
    val formattedAddress: String,
)

@Serializable
data class Location(
    val lat: Double,
    val lng: Double
)