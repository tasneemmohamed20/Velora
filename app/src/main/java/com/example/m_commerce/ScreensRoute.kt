package com.example.m_commerce

import kotlinx.serialization.Serializable

@Serializable
sealed class ScreensRoute {


    @Serializable
    data object Home: ScreensRoute()

    @Serializable
    data object Category: ScreensRoute()

    @Serializable
    object Cart: ScreensRoute()

    @Serializable
    object Order: ScreensRoute()

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

}