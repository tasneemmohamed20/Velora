package com.example.m_commerce.domain.entities


enum class AddressType {
    HOME,
    APARTMENT,
    OFFICE,
}

data class Address(
    var id: String? = null,
    var type: AddressType,
    var area: String,
    var building: String,
    var apartment: String,
    var floor: String?,
    var street: String,
    var phoneNumber: String,
    var additionalDirections: String?,
    var addressLabel: String?,
    var latitude: Double,
    var longitude: Double,
)