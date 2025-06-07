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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.m_commerce.data.remote_data_source.RemoteDataSourceImp
import com.example.m_commerce.data.repository_imp.RepositoryImp
import com.example.m_commerce.domain.usecases.CurrencyExchangeUsecase
import com.example.m_commerce.presentation.Account.settings.view.SettingsScreen
import com.example.m_commerce.presentation.Account.settings.view_model.SettingsViewModel
import com.example.m_commerce.presentation.utils.theme.MCommerceTheme
import com.example.m_commerce.presentation.Account.AccountScreen
import com.example.m_commerce.presentation.HomeScreen
import com.example.m_commerce.presentation.authintication.login.view.LoginScreen
import com.example.m_commerce.presentation.authintication.signUp.view.SignUpScreen
import com.example.m_commerce.data.datasource.remote.product.ProductRemoteDataSourceImp
import com.example.m_commerce.data.repository_imp.products_repo.ProductsRepositoryImp
import com.example.m_commerce.presentation.OrderScreen
import com.example.m_commerce.presentation.ProductsScreen
import com.example.m_commerce.presentation.home.HomeViewModel
import com.example.m_commerce.presentation.home.HomeViewModelFactory
import com.example.m_commerce.presentation.utils.components.BottomNavigationBar
import com.example.m_commerce.start.StartScreen

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

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivity.MainScreen(){

    val showBottomNavBar = remember { mutableStateOf(true) }
    val showTopAppBar = remember { mutableStateOf(true) }

    LaunchedEffect(navHostController) {
        navHostController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.route){
                "com.example.m_commerce.ScreensRoute.Start",
                "com.example.m_commerce.ScreensRoute.Login",
                "com.example.m_commerce.ScreensRoute.SignUp"
                    -> {
                        showBottomNavBar.value = false
                        showTopAppBar.value = false
                    }
                "com.example.m_commerce.ScreensRoute.Settings",
                "com.example.m_commerce.ScreensRoute.Account"-> showTopAppBar.value = false
                else ->{
                    showBottomNavBar.value = true
                    showTopAppBar.value = true
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            if(showBottomNavBar.value){
                BottomNavigationBar {
                    navHostController.navigate(it.route)
                }
            }
        },
        topBar = {
            if(showTopAppBar.value){
                TopAppBar(
                    title = {
                        Text(
                            "Title",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 60.dp),
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
            HomeScreen(
                ViewModelProvider(
                    this@NavHostSetup,
                    HomeViewModelFactory(ProductsRepositoryImp(ProductRemoteDataSourceImp()))
                )
                    [HomeViewModel::class.java])
        }

        composable<ScreensRoute.Cart>{}

        composable<ScreensRoute.Settings>{
            SettingsScreen(
                viewModel = SettingsViewModel(
                    currencyExchangeUsecase = CurrencyExchangeUsecase(
                        repository = RepositoryImp(
                            remoteDataSource = RemoteDataSourceImp()
                        )
                    )
                )
            )
        }

        composable<ScreensRoute.Account>{
            AccountScreen(
                onSettingsClick = {
                    navHostController.navigate(ScreensRoute.Settings)
                }
            )
        }

        composable<ScreensRoute.Products>{
            ProductsScreen()
        }

        composable<ScreensRoute.Order>{
            OrderScreen()
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