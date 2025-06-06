package com.example.m_commerce


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
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
        title = "Favorite",
        icon = Icons.Outlined.FavoriteBorder,
        route = ScreensRoute.Favorites
    ),
    NavigationItem(
        title = "Order",
        icon = Icons.AutoMirrored.Outlined.List,
        route = ScreensRoute.Favorites
    ),
    NavigationItem(
        title = "Account",
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
                alwaysShowLabel = true,
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
                label = {
                    Text(
                        text = navigationItem.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black
                    )
                }
            )
        }
    }

}


















