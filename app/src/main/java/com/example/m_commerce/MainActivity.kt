package com.example.m_commerce

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.m_commerce.presentation.utils.theme.MCommerceTheme
import com.example.m_commerce.presentation.home.HomeScreen
import com.example.m_commerce.presentation.authentication.login.LoginScreen
import com.example.m_commerce.presentation.authentication.signUp.SignUpScreen
import com.example.m_commerce.presentation.OrderScreen
import com.example.m_commerce.presentation.authentication.login.view.LoginScreen
import com.example.m_commerce.presentation.authentication.signUp.view.SignUpScreen
import com.example.m_commerce.presentation.order.OrderScreen
import com.example.m_commerce.presentation.account.AccountScreen
import com.example.m_commerce.presentation.account.settings.view.AddressInfo
import com.example.m_commerce.presentation.account.settings.view.AddressMap
import com.example.m_commerce.presentation.account.settings.view.AddressesScreen
import com.example.m_commerce.presentation.account.settings.view.MapSearch
import com.example.m_commerce.presentation.account.settings.view.SettingsScreen
import com.example.m_commerce.presentation.account.settings.view_model.AddressMapViewModel
import com.example.m_commerce.presentation.account.settings.view_model.AddressesViewModel
import com.example.m_commerce.presentation.account.settings.view_model.SettingsViewModel

import com.example.m_commerce.presentation.products.ProductsScreen
import com.example.m_commerce.presentation.products.ProductsViewModel
import com.example.m_commerce.presentation.utils.components.BottomNavigationBar
import com.example.m_commerce.presentation.utils.routes.ScreensRoute
import com.example.m_commerce.start.StartScreen
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint // marks this activity for Hilt dependency injection
class MainActivity : ComponentActivity() {
    lateinit var navHostController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        enableEdgeToEdge()

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
    var topAppBarTitleState by remember { mutableStateOf("Velora") }
    var showBackButton by remember { mutableStateOf(false) }

    LaunchedEffect(navHostController) {
        navHostController.addOnDestinationChangedListener { _, destination, _ ->
            Log.i(TAG, "MainScreen: route ${destination.route}")
            when(destination.route){
                "com.example.m_commerce.presentation.utils.routes.ScreensRoute.Start",
                "com.example.m_commerce.presentation.utils.routes.ScreensRoute.Login",
                "com.example.m_commerce.presentation.utils.routes.ScreensRoute.SignUp",
                "com.example.m_commerce.presentation.utils.routes.ScreensRoute.AddressMap" ,
                "com.example.m_commerce.presentation.utils.routes.ScreensRoute.MapSearch",
                "com.example.m_commerce.presentation.utils.routes.ScreensRoute.AddressInfo"
                    -> {
                        showBottomNavBar.value = false
                        showTopAppBar.value = false
                        showBackButton = false
                    }
                "com.example.m_commerce.presentation.utils.routes.ScreensRoute.Settings",
                "com.example.m_commerce.presentation.utils.routes.ScreensRoute.Account",
                "com.example.m_commerce.presentation.utils.routes.ScreensRoute.Addresses"->{
                    showBottomNavBar.value = true
                    showTopAppBar.value = false
                }
                "com.example.m_commerce.presentation.utils.routes.ScreensRoute.Products/{type}" -> {
                    showBottomNavBar.value = false
                    showTopAppBar.value = true
                    topAppBarTitleState = "Products"
                    showBackButton = true
                }
                else ->{
                    showBottomNavBar.value = true
                    showTopAppBar.value = true
                    topAppBarTitleState = "Velora"
                    showBackButton = false
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
                    modifier = Modifier.shadow(elevation = 6.dp),
                    title = {
                        Text(
                            topAppBarTitleState,
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
                    navigationIcon = {
                        if(showBackButton){
                            IconButton(onClick = { navHostController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        } else null
                    },
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
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {
            NavHostSetup()
        }
    }
}

@Composable
fun MainActivity.NavHostSetup(){
    NavHost(
        navController = navHostController,
        startDestination = ScreensRoute.Home,
        modifier = Modifier.background(color = Color.White)
    ){
        val viewModel : AddressMapViewModel by viewModels()

        composable<ScreensRoute.Home>{
            HomeScreen(
            ) {
                type -> navHostController.navigate(ScreensRoute.Products(type))
            }
        }

        composable<ScreensRoute.Cart>{}

        composable<ScreensRoute.Settings>{
            SettingsScreen(
                viewModel = SettingsViewModel(
                    currencyExchangeUsecase = CurrencyExchangeUseCase(
                        repository = RepositoryImp(
                            remoteDataSource = RemoteDataSourceImp()
                        )
                    )
                ),
                onAddressClick = {
                    navHostController.navigate(ScreensRoute.Addresses)
                },
                onBackClick = {
                    navHostController.popBackStack()
                }
            )
        }

        composable<ScreensRoute.Account>{
            AccountScreen(
                onSettingsClick = {
                    navHostController.navigate(ScreensRoute.Settings)
                }
            )
        }

        composable<ScreensRoute.Products>{  backStackEntry->
            val entry = backStackEntry.toRoute<ScreensRoute.Products>()
            val type = entry.type
            ProductsScreen(
                type = type
            )
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

        composable<ScreensRoute.Addresses> {
            AddressesScreen(
                viewModel = AddressesViewModel(),
                onBack = {
                    navHostController.popBackStack()
                },
                onAddClicked = {
                    navHostController.navigate(ScreensRoute.AddressMap)
                    Log.d(TAG, "NavHostSetup: $it")
                }
            )
        }

        composable<ScreensRoute.AddressMap> {

            AddressMap(
                onSearchClicked = {
                    navHostController.navigate(ScreensRoute.MapSearch)
                },
                onBackClick = {
                    navHostController.popBackStack()
                },
                onConfirmLocation = {},
                viewModel = viewModel
            )
        }

        composable<ScreensRoute.MapSearch> {
            MapSearch(
                onBack = { navHostController.popBackStack() },
                onResultClick = { latLng ->
                    navHostController.popBackStack()
                },
                viewModel = viewModel

            )
        }
    }
}