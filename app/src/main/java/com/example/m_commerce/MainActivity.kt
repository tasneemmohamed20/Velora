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
import com.example.m_commerce.ui.theme.MCommerceTheme
import com.example.m_commerce.ui.view.AccountScreen
import com.example.m_commerce.ui.view.CategoryScreen
import com.example.m_commerce.ui.view.HomeScreen


private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    lateinit var navHostController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

//        GlobalScope.launch {
//            val apolloClient = ApolloClient.Builder()
//                .serverUrl("https://and2-ism-mad45.myshopify.com/api/2025-04/graphql.json")
//                .addHttpHeader("X-Shopify-Storefront-Access-Token", "da2a10babb2984a38271fe2d887ed128")
//                .addHttpHeader("Content-Type", "application/json")
//                .build()
//            try {
//                val response = apolloClient.query(GetProductsQuery()).execute()
//                val products = response.data?.products?.edges?.map { it?.node }
//                products?.forEach {
//                    Log.d(TAG, it?.title ?: "No title")
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "Failed: ${e.message}")
//            }
//        }


//         GlobalScope.launch {
//             val apolloClient = ApolloClient.Builder()
//                 .serverUrl("https://and2-ism-mad45.myshopify.com/api/2025-04/graphql")
//                 .addHttpHeader("X-Shopify-Storefront-Access-Token", "da2a10babb2984a38271fe2d887ed128")
// //                .addHttpHeader("Content-Type", "application/json")
//                 .build()
//             try {
//                 val response = apolloClient.query(GetProductsQuery()).execute()
//                 val products = response.data?.products?.edges?.map { it?.node }
//                 products?.forEach {
//                     Log.d(TAG, it?.title ?: "No title")
//                 }

//                 val ress = apolloClient.query(GetLocalizationOptionsQuery()).execute()
//                 val localizationOptions = ress.data?.localization?.availableCountries?.map { it.availableLanguages }
//                 if (localizationOptions != null) {
//                 localizationOptions?.forEach { languages ->
//                     languages?.forEach { language ->
//                         Log.d(TAG, "Language: ${language?.name} ")
//                     }
//                 }
//                 }else{
//                     Log.d(TAG, "No localization options found")
//                 }
//             } catch (e: Exception) {
//                 Log.e(TAG, "Failed: ${e.message}")
//             }
//         }

        setContent {
            MCommerceTheme {

                navHostController = rememberNavController()
                MainScreen()
            }
        }
    }




}

@Composable
fun MainActivity.NavHostSetup(){
    NavHost(
        navController = navHostController,
        startDestination = ScreensRoute.Home
    ){
        composable<ScreensRoute.Home>{
            HomeScreen()
        }

        composable<ScreensRoute.Category>{
            CategoryScreen()
        }

        composable<ScreensRoute.Favorites>{

        }

        composable<ScreensRoute.Account>{
            AccountScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivity.MainScreen(){

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
                ) },
                colors = TopAppBarColors(
                    containerColor = Color.White,
                    scrolledContainerColor = Color.Black,
                    navigationIconContentColor = Color.Black,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black,
                ),
                actions = {

                    IconButton(onClick = {  }) {
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

