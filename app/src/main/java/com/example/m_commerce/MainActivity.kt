package com.example.m_commerce

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.m_commerce.data.datasource.remote.product.ProductRemoteDataSourceImp
import com.example.m_commerce.data.repo_imp.ProductsRepositoryImp
import com.example.m_commerce.ui.theme.MCommerceTheme
import com.example.m_commerce.ui.view.AccountScreen
import com.example.m_commerce.ui.category.ProductsScreen
import com.example.m_commerce.ui.home.HomeScreen
import com.example.m_commerce.ui.home.HomeViewModel
import com.example.m_commerce.ui.home.HomeViewModelFactory



private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {


    lateinit var navHostController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

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
            HomeScreen(ViewModelProvider(
                this@NavHostSetup,
                HomeViewModelFactory(ProductsRepositoryImp(ProductRemoteDataSourceImp()))
               )
                [HomeViewModel::class.java])
        }

        composable<ScreensRoute.Products>{
            ProductsScreen()
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
                        modifier = Modifier.fillMaxWidth().padding(start = 60.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.W700)
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

