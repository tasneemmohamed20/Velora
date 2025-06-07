package com.example.m_commerce

import kotlinx.serialization.Serializable

@Serializable
sealed class ScreensRoute {

    @Serializable
    data object Home: ScreensRoute()

    @Serializable
    class Products(val handle: String): ScreensRoute()

    @Serializable
    object Cart: ScreensRoute()

    @Serializable
    object Order: ScreensRoute()

    @Serializable
    object Favorites: ScreensRoute()

    @Serializable
    object Account: ScreensRoute()

}