package com.example.m_commerce.presentation.utils.routes

import com.example.m_commerce.domain.entities.OrderEntity
import kotlinx.serialization.Serializable

@Serializable
sealed class ScreensRoute {


    @Serializable
    data object Home: ScreensRoute()

    @Serializable
    data class Products(val type: String): ScreensRoute()

    @Serializable
    object Cart: ScreensRoute()

    @Serializable
    object Order: ScreensRoute()

    @Serializable
    data object OrderDetails: ScreensRoute()

    @Serializable
    object Favorites: ScreensRoute()

    @Serializable
    object Account: ScreensRoute()

    @Serializable

    object Settings: ScreensRoute()

    @Serializable
    data object Start : ScreensRoute()

    @Serializable
    data object Login : ScreensRoute()

    @Serializable
    data object SignUp : ScreensRoute()

    @Serializable
    data object Addresses : ScreensRoute()

    @Serializable
    data object AddressMap : ScreensRoute()

    @Serializable
    data object MapSearch : ScreensRoute()

    @Serializable
    data object AddressInfo : ScreensRoute()

    @Serializable
    data object Search : ScreensRoute()

    @Serializable
    class ProductDetails(val productId: String): ScreensRoute()
}