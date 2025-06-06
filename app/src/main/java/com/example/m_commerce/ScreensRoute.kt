package com.example.m_commerce

import kotlinx.serialization.Serializable

@Serializable
sealed class ScreensRoute(val route: String) {
    @Serializable data object Start : ScreensRoute("start")
    @Serializable data object Login : ScreensRoute("login")
    @Serializable data object SignUp : ScreensRoute("signup")
    @Serializable data object Home : ScreensRoute("home")
    @Serializable data object Category : ScreensRoute("category")
    @Serializable data object Cart : ScreensRoute("cart")
    @Serializable data object Order : ScreensRoute("order")
    @Serializable data object Favorites : ScreensRoute("favorites")
    @Serializable data object Account : ScreensRoute("account")
}