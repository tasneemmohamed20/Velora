package com.example.m_commerce.presentation.utils.Functions

import android.util.Log
import com.example.m_commerce.domain.entities.Product

fun formatTitleAndBrand(title: String): Pair<String, String> {
    val parts = title.split(" | ").map { it.trim() }
    return if (parts.size > 1) {
        val brand = parts.first()
        val productName = parts.drop(1).joinToString(" ")
        brand to productName
    } else {
        "" to title
    }
}


fun filterProductsBySubType(products: MutableList<Product>?, subType: String) =
    products?.filter { it.productType == subType }

