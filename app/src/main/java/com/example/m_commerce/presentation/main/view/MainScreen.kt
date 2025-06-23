package com.example.m_commerce.presentation.main.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.m_commerce.MainActivity
import com.example.m_commerce.NavHostSetup
import com.example.m_commerce.presentation.main.viewmodel.MainViewModel
import com.example.m_commerce.presentation.utils.components.BottomNavigationBar
import com.example.m_commerce.presentation.utils.components.CustomSnackbar
import com.example.m_commerce.presentation.utils.routes.ScreensRoute


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivity.MainScreen(mainViewModel: MainViewModel = hiltViewModel()){

    val showBottomNavBar = remember { mutableStateOf(true) }
    val showTopAppBar = remember { mutableStateOf(true) }
    var topAppBarTitleState by remember { mutableStateOf("Velora") }
    var showBackButton by remember { mutableStateOf(false) }
    val isConnected by mainViewModel.isConnected.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var previousConnectionState by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(isConnected) {
        if (previousConnectionState != null && previousConnectionState != isConnected) {
            snackbarHostState.showSnackbar(
                message = if (isConnected)
                    "Your internet connection was restored"
                else
                    "You are currently offline",
                duration = SnackbarDuration.Short
            )
        }
        previousConnectionState = isConnected
    }

    LaunchedEffect(navHostController) {
        navHostController.addOnDestinationChangedListener { _, destination, _ ->

            when {
                destination.route in listOf(
                    "com.example.m_commerce.presentation.utils.routes.ScreensRoute.Start",
                    "com.example.m_commerce.presentation.utils.routes.ScreensRoute.Login",
                    "com.example.m_commerce.presentation.utils.routes.ScreensRoute.SignUp",
                    "com.example.m_commerce.presentation.utils.routes.ScreensRoute.AddressMap",
                    "com.example.m_commerce.presentation.utils.routes.ScreensRoute.MapSearch",
                    "com.example.m_commerce.presentation.utils.routes.ScreensRoute.AddressInfo",
                    "com.example.m_commerce.presentation.utils.routes.ScreensRoute.Cart"
                ) || destination.route?.startsWith("checkout") == true -> {
                    showBottomNavBar.value = false
                    showTopAppBar.value = false
                    showBackButton = false
                }
                destination.route in listOf(
                    "com.example.m_commerce.presentation.utils.routes.ScreensRoute.Settings",
                    "com.example.m_commerce.presentation.utils.routes.ScreensRoute.Account",
                    "com.example.m_commerce.presentation.utils.routes.ScreensRoute.Addresses",
                    "com.example.m_commerce.presentation.utils.routes.ScreensRoute.VouchersScreen"
                ) -> {
                    showBottomNavBar.value = true
                    showTopAppBar.value = false
                }
                destination.route in listOf(
                    "com.example.m_commerce.presentation.utils.routes.ScreensRoute.Products/{type}",
                    "com.example.m_commerce.presentation.utils.routes.ScreensRoute.Search",
                    "com.example.m_commerce.presentation.utils.routes.ScreensRoute.ProductDetails/{productId}"
                ) -> {
                    showBottomNavBar.value = false
                    showTopAppBar.value = true
                    topAppBarTitleState = "Products"
                    showBackButton = true
                }
                destination.route in listOf(
                    "com.example.m_commerce.presentation.utils.routes.ScreensRoute.OrderDetails",
                    "com.example.m_commerce.presentation.utils.routes.ScreensRoute.Order") -> {
                    showBottomNavBar.value = false
                    showTopAppBar.value = true
                    topAppBarTitleState = "My Orders"
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
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(12.dp)
            ){
                snackbarData ->  Snackbar(
                    shape = RoundedCornerShape(size = 16.dp),
                    containerColor = if(isConnected) Color(0xff31C440) else Color.Black.copy(alpha = 0.8f)
                ){
                    CustomSnackbar(
                        message = snackbarData.visuals.message,
                        icon = if (isConnected) Icons.Default.CheckCircle else Icons.Default.WifiOff
                    )
                }
            }
        },
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
                        }
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
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            NavHostSetup()
        }
    }


}

@Composable
fun NoInternetLottie(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("no_internet.json"))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        LottieAnimation(
            composition = composition,
            progress = progress
        )
    }
}


