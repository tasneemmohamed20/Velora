package com.example.m_commerce.presentation.utils.Functions

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import com.example.m_commerce.domain.entities.Product
import java.time.ZonedDateTime

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

@RequiresApi(Build.VERSION_CODES.O)
fun formatShopifyDate(input: String): String {
    val date = ZonedDateTime.parse(input)

    val dayOfWeek = date.dayOfWeek.toString().lowercase().replaceFirstChar { it.uppercase() }.take(3)
    val month = date.month.toString().lowercase().replaceFirstChar { it.uppercase() }
    val day = date.dayOfMonth
    val suffix = when (day) {
        1, 21, 31 -> "st"
        2, 22     -> "nd"
        3, 23     -> "rd"
        else      -> "th"
    }

    return "$dayOfWeek. $month $day$suffix ${date.year}"
}

fun mapOrderStatusSimple(financialStatus: String?, fulfillmentStatus: String?): String {
    return when (financialStatus to fulfillmentStatus) {
        "PAID" to "FULFILLED" -> "Completed"
        "PAID" to "PARTIALLY_FULFILLED" -> "Partial"
        "PAID" to "UNFULFILLED" -> "Unshipped"
        "PAID" to "IN_PROGRESS" -> "Shipping"
        "UNPAID" to "UNFULFILLED" -> "Pending"
        "REFUNDED" to "UNFULFILLED" -> "Refunded"
        "REFUNDED" to "FULFILLED" -> "Refunded"
        "VOIDED" to "UNFULFILLED" -> "Voided"
        "PARTIALLY_PAID" to "PARTIALLY_FULFILLED" -> "Partial"
        "PARTIALLY_REFUNDED" to "FULFILLED" -> "Partial"
        "PAID" to "SCHEDULED" -> "Scheduled"
        "PAID" to "ON_HOLD" -> "OnHold"
        "PAID" to "RESTOCKED" -> "Restocked"
        "PAID" to "CANCELED" -> "Canceled"
        else -> "Processing"
    }
}

fun getOrderStatusColors(status: String): Color {
    return when (status) {
        "Completed" -> Color(0xFF4CAF50)
        "Unshipped" -> Color(0xFFFFC107)
        "Shipping" -> Color(0xFF2196F3)
        "Pending" -> Color(0xFF9E9E9E)
        "Refunded" -> Color(0xFFF44336)
        "Voided" -> Color(0xFF616161)
        "Partial" -> Color(0xFFFF9800)
        "Scheduled" -> Color(0xFF3F51B5)
        "OnHold" -> Color(0xFF795548)
        "Restocked" -> Color(0xFF009688)
        "Canceled" -> Color(0xFFB71C1C)
        else -> Color.Black
    }
}

fun formatStreetAndArea(raw: String): String {
    val parts = raw.split("|").associate {
        val (key, value) = it.split(":")
        key to value
    }

    val street = parts["street"] ?: ""
    val area = parts["area"] ?: ""

    return "$street St.\n$area"
}
