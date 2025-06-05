package com.example.m_commerce


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: ScreensRoute
)


val navigationItems = listOf(
    NavigationItem(
        title = "Home",
        icon = Icons.Outlined.Home,
        route = ScreensRoute.Home
    ),
    NavigationItem(
        title = "Category",
        icon = Icons.Default.ShoppingCart,
        route = ScreensRoute.Categories
    ),
    NavigationItem(
        title = "Profile",
        icon = Icons.Outlined.Person,
        route = ScreensRoute.Account
    ),
)


@Composable
fun BottomNavigationBar(onItemSelected: (NavigationItem) -> Unit){

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 30.dp
    ){
        var selectedNavigationIndex by rememberSaveable {
            mutableIntStateOf(0)
        }

        navigationItems.forEachIndexed { index, navigationItem ->

            NavigationBarItem(
                selected = selectedNavigationIndex == index,
                onClick = {
                    selectedNavigationIndex = index
                    onItemSelected(navigationItem)
                },
                icon = {
                    Icon(
                        imageVector = navigationItem.icon,
                        contentDescription = navigationItem.title,
                    )
                },
            )
        }
    }

}


















