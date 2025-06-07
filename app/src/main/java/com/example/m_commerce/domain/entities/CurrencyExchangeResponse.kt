package com.example.m_commerce.domain.entities

data class CurrencyExchangeResponse(
    val base: String,
    val date: String,
    val rates: Rates
)

data class Rates(
    val EGP: String,
)