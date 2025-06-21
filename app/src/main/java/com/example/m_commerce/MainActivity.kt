package com.example.m_commerce

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
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
import com.example.m_commerce.presentation.account.AccountScreen
import com.example.m_commerce.presentation.account.settings.view.AddressInfo
import com.example.m_commerce.presentation.account.settings.view.AddressMap
import com.example.m_commerce.presentation.account.settings.view.AddressesScreen
import com.example.m_commerce.presentation.account.settings.view.MapSearch
import com.example.m_commerce.presentation.account.settings.view.SettingsScreen
import com.example.m_commerce.presentation.account.settings.view_model.AddressMapViewModel
import com.example.m_commerce.presentation.authentication.login.LoginScreen
import com.example.m_commerce.presentation.authentication.signUp.SignUpScreen
import com.example.m_commerce.presentation.cart.CartScreen
import com.example.m_commerce.presentation.home.HomeScreen
import com.example.m_commerce.presentation.order.OrderScreen
import com.example.m_commerce.presentation.productDetails.ProductDetailsScreen
import com.example.m_commerce.presentation.products.ProductsScreen
import com.example.m_commerce.presentation.search.SearchScreen
import com.example.m_commerce.presentation.start.StartScreen
import com.example.m_commerce.presentation.utils.components.BottomNavigationBar
import com.example.m_commerce.presentation.utils.routes.ScreensRoute
import com.example.m_commerce.presentation.utils.theme.MCommerceTheme
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint // marks this activity for Hilt dependency injection
class MainActivity : ComponentActivity() {
    lateinit var navHostController: NavHostController
    @RequiresApi(Build.VERSION_CODES.O)
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



@RequiresApi(Build.VERSION_CODES.O)
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
                "com.example.m_commerce.presentation.utils.routes.ScreensRoute.AddressInfo",
                "com.example.m_commerce.presentation.utils.routes.ScreensRoute.Cart",
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
                "com.example.m_commerce.presentation.utils.routes.ScreensRoute.Products/{type}" ,
                "com.example.m_commerce.presentation.utils.routes.ScreensRoute.Search" ,
                "com.example.m_commerce.presentation.utils.routes.ScreensRoute.ProductDetails/{productId}" -> {
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
                        IconButton(onClick = { navHostController.navigate(ScreensRoute.Search) }) {
                            Icon(Icons.Outlined.Search, contentDescription = "Search Product")
                        }
                        IconButton(onClick = { navHostController.navigate(ScreensRoute.Cart) }) {
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainActivity.NavHostSetup(){
    NavHost(
        navController = navHostController,
        startDestination = ScreensRoute.Start,
        modifier = Modifier.background(color = Color.White)
    ){
        val viewModel : AddressMapViewModel by viewModels()

        composable<ScreensRoute.Home>{
            HomeScreen(
            ) {
                type -> navHostController.navigate(ScreensRoute.Products(type))
            }
        }

        composable<ScreensRoute.Cart>{
            CartScreen(
                onBack = { navHostController.popBackStack() },
                onAddItems = { navHostController.popBackStack() },
                onCheckout = {},
            )
        }

        composable<ScreensRoute.Favorites>{

        }

        composable<ScreensRoute.Settings>{
            SettingsScreen(
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
                },
                onOrderClick = {
                    navHostController.navigate(ScreensRoute.Order)
                }
            )
        }

        composable<ScreensRoute.Products>{  backStackEntry->
            val entry = backStackEntry.toRoute<ScreensRoute.Products>()
            val type = entry.type
            ProductsScreen(
                type = type,
                onProductClick = { productId ->
                    navHostController.navigate(ScreensRoute.ProductDetails(productId))
                }
            )
        }

        composable<ScreensRoute.Order>{
            OrderScreen()
        }


        composable<ScreensRoute.Start> {
            StartScreen(
                onEmailClicked = { navHostController.navigate(ScreensRoute.Login) },
                onGoogleSuccess = {
                    navHostController.navigate(ScreensRoute.Home) {
                        popUpTo(ScreensRoute.Start) { inclusive = true }
                    }
                }
            )
        }

        composable<ScreensRoute.Login> {
            LoginScreen(
                onButtonClicked = { navHostController.navigate(ScreensRoute.SignUp)},
                onLoginSuccess = {
                    navHostController.navigate(ScreensRoute.Home) {
                        popUpTo(ScreensRoute.Start) { inclusive = true }
                    }
                }
            )
        }

        composable<ScreensRoute.SignUp> {
            SignUpScreen(onButtonClicked = {
                navHostController.navigate(ScreensRoute.Login)
            })
        }

        composable<ScreensRoute.Addresses> {
            AddressesScreen(
                viewModel = viewModel,
                onBack = {
                    navHostController.popBackStack()
                },
                onAddClicked = {
                    navHostController.navigate(ScreensRoute.AddressMap)
                    Log.d(TAG, "NavHostSetup: $it")
                },
                onAddressClick = { address ->
                    viewModel.setEditingAddress(address)
                    viewModel.updateCurrentLocation(
                        LatLng(address.latitude, address.longitude)
                    )
                    navHostController.navigate(ScreensRoute.AddressInfo)
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
                onConfirmLocation = {
                    navHostController.navigate(ScreensRoute.AddressInfo)
                },
                viewModel = viewModel,
                isFromEdit = navHostController.previousBackStackEntry?.destination?.route == ScreensRoute.AddressInfo.toString()

            )
        }

        composable<ScreensRoute.Search> {
            SearchScreen(
                onBack = { navHostController.popBackStack() },
                onProductClick = { productId ->
                    navHostController.navigate(ScreensRoute.ProductDetails(productId))
                }
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

        composable<ScreensRoute.AddressInfo> {
            AddressInfo(
                onBack = { navHostController.popBackStack() },
                onSave = { address ->
                    viewModel.saveAddressToCustomer(address)
                    navHostController.popBackStack(
                        "com.example.m_commerce.presentation.utils.routes.ScreensRoute.Addresses",
                        inclusive = false
                    )
                },
                viewModel = viewModel,
                goToMap = {
                    navHostController.navigate(ScreensRoute.AddressMap) {
                        popUpTo(ScreensRoute.AddressInfo) { inclusive = true }
                    }
                }
            )
        }

        composable<ScreensRoute.ProductDetails> { backStackEntry ->
            val entry = backStackEntry.toRoute<ScreensRoute.ProductDetails>()
            ProductDetailsScreen(
                productId = entry.productId,
                onBack = { navHostController.popBackStack() }
            )
        }
    }
}