package com.example.m_commerce.presentation.main.view

import android.net.Uri
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.payment.OrderItem
import com.example.m_commerce.domain.entities.OrderEntity
import com.example.m_commerce.presentation.on_boarding.OnBoardingViewModel
import com.example.m_commerce.presentation.on_boarding.OnBoardingScreens
import com.example.m_commerce.presentation.order.orders_list.OrderScreen
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
import com.example.m_commerce.presentation.favorite.FavoriteView
import com.example.m_commerce.presentation.home.HomeScreen
import com.example.m_commerce.presentation.main.viewmodel.MainViewModel
import com.example.m_commerce.presentation.order.order_details.OrderDetails
import com.example.m_commerce.presentation.payment.checkout.CheckoutScreen
import com.example.m_commerce.presentation.payment.checkout.PaymentMethod
import com.example.m_commerce.presentation.payment.payment.PaymentScreen
import com.example.m_commerce.presentation.productDetails.ProductDetailsScreen
import com.example.m_commerce.presentation.products.ProductsScreen
import com.example.m_commerce.presentation.splash.SplashScreen
import com.example.m_commerce.presentation.search.SearchScreen
import com.example.m_commerce.presentation.start.StartScreen
import com.example.m_commerce.presentation.utils.routes.ScreensRoute
import com.example.m_commerce.presentation.utils.theme.MCommerceTheme
import com.example.m_commerce.presentation.vouchers.VouchersScreen
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.serialization.json.Json


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var navHostController: NavHostController


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MCommerceTheme(dynamicColor = false) {
                navHostController = rememberNavController()
                MainScreen()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainActivity.NavHostSetup(mainViewModel: MainViewModel, isLogged: Boolean){

    NavHost(
        navController = navHostController,
        startDestination = ScreensRoute.Splash,
        modifier = Modifier.background(color = Color.White)
    ){
        val viewModel : AddressMapViewModel by viewModels()
        val onBoardingViewModel: OnBoardingViewModel by viewModels()

        composable<ScreensRoute.Splash> {
            SplashScreen(
                onNavigateToStart = {
                    if(isLogged) {
                        navHostController.navigate(ScreensRoute.Home) {
                            popUpTo(ScreensRoute.Splash) { inclusive = true }
                        }
                    }else
                    {
                        navHostController.navigate(ScreensRoute.Start) {
                            popUpTo(ScreensRoute.Splash) { inclusive = true }
                        }
                    }
                },
                onNavigateToOnBoarding = {
                    navHostController.navigate(ScreensRoute.OnBoarding) {
                        popUpTo(ScreensRoute.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable<ScreensRoute.Home> {
            HomeScreen(
            ) {
                type -> navHostController.navigate(ScreensRoute.Products(type))
            }
        }

        composable<ScreensRoute.Cart> {
            CartScreen(
                onBack = { navHostController.popBackStack() },
                onCheckout = { order, totalAmountCents, subtotal, estimatedFee, itemsCount ->
                    val orderItems = order.lineItems?.nodes?.map { node ->
                        OrderItem(
                            name = node.title ?: node.name ?: "Unknown Item",
                            description = node.title ?: "",
                            amountCents = (totalAmountCents.times(100)).toDouble(),
                            quantity = node.quantity ?: 1,
                            itemId = node.id ?: ""
                        )
                    } ?: emptyList()

                    val itemsJson = Uri.encode(Json.encodeToString(ArrayList(orderItems)))
                    navHostController.navigate("checkout/$itemsJson/$totalAmountCents/$subtotal/$estimatedFee/$itemsCount")
                }
            )
        }

        composable(
            route = "checkout/{items}/{totalAmountCents}/{subtotal}/{estimatedFee}/{itemsCount}",
            arguments = listOf(
                navArgument("items") { type = NavType.StringType },
                navArgument("totalAmountCents") { type = NavType.IntType },
                navArgument("subtotal") { type = NavType.StringType },
                navArgument("estimatedFee") { type = NavType.StringType },
                navArgument("itemsCount") { type = NavType.IntType }
            )
        ) {

                backStackEntry ->
            val itemsJson = backStackEntry.arguments?.getString("items") ?: "[]"
            val totalAmountCents = backStackEntry.arguments?.getInt("totalAmountCents") ?: 0
            val items = Json.decodeFromString<List<OrderItem>>(Uri.decode(itemsJson))
            val subtotal = backStackEntry.arguments?.getString("subtotal")?.toDoubleOrNull() ?: 0.0
            val estimatedFee =
                backStackEntry.arguments?.getString("estimatedFee")?.toDoubleOrNull() ?: 0.0
            val itemsCount = backStackEntry.arguments?.getInt("itemsCount") ?: 0
            CheckoutScreen(
                onBack = { navHostController.popBackStack() },
                totalPrice = totalAmountCents / 100.0,
                items = items,
                onConfirmOrder = { paymentMethod, orderItems, totalAmountCents ->
                    when (paymentMethod) {
                        PaymentMethod.PAYMOB -> {
                            val itemsJson = Uri.encode(Json.encodeToString(ArrayList(orderItems)))
                            navHostController.navigate("payment/$itemsJson/$totalAmountCents")
                        }

                        PaymentMethod.CASH -> {

                        }
                    }
                },
                subtotal = subtotal,
                estimatedFee = estimatedFee,
                itemsCount = itemsCount,
                onOrderCompleted = {
                    navHostController.navigate(ScreensRoute.Home)
                },
                onNavigateToAddresses = {
                    navHostController.navigate(ScreensRoute.Addresses)
                }
            )
        }

        composable<ScreensRoute.Settings> {

            SettingsScreen(
                onAddressClick = {
                    navHostController.navigate(ScreensRoute.Addresses)
                },
                onBackClick = {
                    navHostController.popBackStack()
                },
                onLogoutClicked = {
                    mainViewModel.logout()
                    navHostController.navigate(ScreensRoute.Start) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<ScreensRoute.Account> {
            AccountScreen(
                onSettingsClick = {
                    navHostController.navigate(ScreensRoute.Settings)
                },
                onOrderClick = {
                    navHostController.navigate(ScreensRoute.Order)
                },
                onVeloraVouchersClick = { navHostController.navigate(ScreensRoute.VouchersScreen) },
                onCartClicked = { navHostController.navigate(ScreensRoute.Cart) },
                onFavoritesClick = { navHostController.navigate(ScreensRoute.Favorites) },
            )
        }

        composable<ScreensRoute.Addresses> {
            AddressesScreen(
                viewModel = viewModel,
                onBack = {
                    navHostController.popBackStack()
                },
                onAddClicked = {
                    viewModel.resetForAddMode()
                    navHostController.navigate(ScreensRoute.AddressMap)
                },
                onAddressClick = { address ->
                    viewModel.setupForEditMode(address)
                    navHostController.navigate(ScreensRoute.AddressInfo)
                },
                navController = navHostController,
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
                    viewModel.editingAddress.value?.let { address ->
                        viewModel.setupForEditMode(address)
                    }
                    navHostController.navigate(ScreensRoute.AddressMap)
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
                onConfirmLocation = { address ->
                    navHostController.navigate(ScreensRoute.AddressInfo)
                },
                viewModel = viewModel,
                isFromEdit = !viewModel.isAddMode.collectAsState().value
            )
        }

        composable<ScreensRoute.Products>{  backStackEntry->
            val entry = backStackEntry.toRoute<ScreensRoute.Products>()
            ProductsScreen(
                type = entry.type,
                onProductClick = { productId ->
                    navHostController.navigate(ScreensRoute.ProductDetails(productId))
                }
            )
        }

        composable<ScreensRoute.Order> {
            OrderScreen(onOrderClicked = { order ->
                navHostController.currentBackStackEntry?.savedStateHandle?.set("order", order)
                navHostController.navigate(ScreensRoute.OrderDetails)
            },
                onExploreProductsClicked = {
                    navHostController.navigate(ScreensRoute.Search)
                })
        }

        composable<ScreensRoute.Favorites> {
            FavoriteView(
                onProductClick = { productId ->
                    Log.d("ProductClick", "Navigating with productId: $productId")
                    navHostController.navigate(ScreensRoute.ProductDetails(productId))
                }
            )
        }

        composable<ScreensRoute.OrderDetails>{
            val order = navHostController.previousBackStackEntry?.savedStateHandle?.get<OrderEntity>("order")
            if (order != null) {
                OrderDetails(order)
            }
        }

        composable<ScreensRoute.Start> {
            StartScreen(
                onEmailClicked = { navHostController.navigate(ScreensRoute.Login) },
                onGoogleSuccess = {
                    mainViewModel.setLogged()
                    navHostController.navigate(ScreensRoute.Home) {
                        popUpTo(ScreensRoute.Start) { inclusive = true }
                    }
                },
                onGuestSuccess = {
                    mainViewModel.setLogged()
                    navHostController.navigate(ScreensRoute.Home) {
                        popUpTo(ScreensRoute.Start) { inclusive = false }
                    }
                },
            )
        }

        composable<ScreensRoute.Login> {
            LoginScreen(
                onButtonClicked = { navHostController.navigate(ScreensRoute.SignUp)},
                onLoginSuccess = {
                    mainViewModel.setLogged()
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

        composable<ScreensRoute.ProductDetails> { backStackEntry ->
            val entry = backStackEntry.toRoute<ScreensRoute.ProductDetails>()
            ProductDetailsScreen(
                productId = entry.productId,
                onBack = { navHostController.popBackStack() },
                navController = navHostController

            )
        }

        composable(
            route = "payment/{items}/{totalAmountCents}",
            arguments = listOf(
                navArgument("items") { type = NavType.StringType },
                navArgument("totalAmountCents") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val itemsJson = backStackEntry.arguments?.getString("items") ?: "[]"
            val totalAmountCents = backStackEntry.arguments?.getInt("totalAmountCents") ?: 0
            val items = Json.decodeFromString<List<OrderItem>>(Uri.decode(itemsJson))

            PaymentScreen(
                items = items,
                totalAmountCents = totalAmountCents,
                onPaymentComplete = {
                    navHostController.navigate(ScreensRoute.Home)
                },
                onPaymentError = { Log.i("PaymentScreen", "onPaymentError: ") }
            )
        }

        composable<ScreensRoute.VouchersScreen> {
            VouchersScreen(
                onBackClick = { navHostController.popBackStack() },
            )
        }

        composable<ScreensRoute.OnBoarding> {
            OnBoardingScreens(
                onFinish = {
                    onBoardingViewModel.completeOnboarding()
                    navHostController.navigate(ScreensRoute.Start) {
                        popUpTo(ScreensRoute.OnBoarding) { inclusive = true }
                    }
                }
            )
        }
    }
}
