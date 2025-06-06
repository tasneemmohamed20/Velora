package com.example.m_commerce

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.apollographql.apollo.ApolloClient
import com.example.m_commerce.authintication.login.view.LoginScreen
import com.example.m_commerce.authintication.signUp.view.SignUpScreen
import com.example.m_commerce.start.StartScreen
import com.example.m_commerce.ui.theme.MCommerceTheme
import com.example.m_commerce.ui.view.AccountScreen
import com.example.m_commerce.ui.view.CategoryScreen
import com.example.m_commerce.ui.view.HomeScreen
import com.example.m_commerce.ui.view.OrderScreen


private const val TAG = "MainActivity"
class MainActivity : ComponentActivity() {
    lateinit var navHostController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            navHostController = rememberNavController()
            MCommerceTheme {
                MainScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen() {

        Scaffold(
            bottomBar = {
                BottomNavigationBar {
                    navHostController.navigate(it.route)
                }
            },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Title",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarColors(
                        containerColor = Color.White,
                        scrolledContainerColor = Color.Black,
                        navigationIconContentColor = Color.Black,
                        titleContentColor = Color.Black,
                        actionIconContentColor = Color.Black,
                    ),
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Outlined.Search, contentDescription = "Search Product")
                        }
                        IconButton(onClick = { }) {
                            Icon(Icons.Outlined.ShoppingCart, contentDescription = "ShoppingCart")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                NavHostSetup()
            }
        }
    }

    @Composable
    fun MainActivity.NavHostSetup(){
        NavHost(
            navController = navHostController,
            startDestination = ScreensRoute.Start
        ){
            composable<ScreensRoute.Home>{
                HomeScreen()
            }

            composable<ScreensRoute.Category>{
                CategoryScreen()
            }

            composable<ScreensRoute.Order>{
                OrderScreen()
            }

            composable<ScreensRoute.Account>{
                AccountScreen()
            }

            composable<ScreensRoute.Start> {
                StartScreen(onEmailClicked = {
                    navHostController.navigate(ScreensRoute.Login)
                })
            }
            composable<ScreensRoute.Login> {
                LoginScreen(onButtonClicked = {
                    navHostController.navigate(ScreensRoute.SignUp)
                })
            }
            composable<ScreensRoute.SignUp> {
                SignUpScreen(onButtonClicked = {
                    navHostController.navigate(ScreensRoute.Login)
                })
            }
        }
    }
}