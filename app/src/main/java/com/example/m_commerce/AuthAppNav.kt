package com.example.m_commerce

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.m_commerce.authintication.login.view.LoginScreen
import com.example.m_commerce.authintication.signUp.view.SignUpScreen
import com.example.m_commerce.start.StartScreen

@Composable
fun AuthAppNav() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.START.route

    ) {
        composable(Routes.START.route) { StartScreen(navController) }
        composable(Routes.LOGIN.route) { LoginScreen(navController) }
        composable(Routes.SIGNUP.route) { SignUpScreen(navController) }
    }
}
