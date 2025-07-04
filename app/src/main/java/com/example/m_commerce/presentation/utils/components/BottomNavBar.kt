package com.example.m_commerce.presentation.utils.components


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.m_commerce.presentation.utils.routes.ScreensRoute
import com.example.m_commerce.presentation.utils.theme.Primary
import com.example.m_commerce.presentation.utils.theme.Secondary

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
        title = "Account",
        icon = Icons.Outlined.Person,
        route = ScreensRoute.Account
    ),
)


@Composable
fun BottomNavigationBar(
    selectedIndex: Int,
    onItemSelected: (index: Int, item: NavigationItem) -> Unit
){

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 30.dp,
        modifier = Modifier.shadow(elevation = 8.dp),

    ){

        navigationItems.forEachIndexed { index, navigationItem ->

            NavigationBarItem(
                alwaysShowLabel = true,
                colors = NavigationBarItemColors(
                    selectedIndicatorColor = Primary.copy(alpha = .1f),
                    selectedIconColor = Primary,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.Black,
                    unselectedTextColor = Color.Black,
                    disabledIconColor = Secondary,
                    disabledTextColor = Color.White
                ),
                selected = selectedIndex  == index,
                onClick = {
                    onItemSelected(index, navigationItem)
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
                        color = if (selectedIndex  == index) Primary else  Color.Black
                    )
                },

            )
        }
    }
}


















