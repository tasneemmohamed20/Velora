package com.example.m_commerce.presentation.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce.R

@Composable
fun AccountScreen(
    onSettingsClick: () -> Unit = {},
    onOrderClick: () -> Unit = {},
    onOffersClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onVeloraVouchersClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    viewModel: AccountViewModel = hiltViewModel()
) {

    val customer by viewModel.customerState.collectAsState()
    val customerName = customer?.displayName ?: "Guest"
    val avatarLetter = customer?.firstName?.firstOrNull()?.toString()?.uppercase() ?: "G"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3669C9).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarLetter,
                    color = Color.DarkGray,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(2f)) {
                Text("Hi $customerName", fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color.Black)
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Icon(
                        painter = painterResource(id = R.drawable.adidas),
                        contentDescription = "UAE Flag",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("UAE", fontSize = 12.sp, color = Color.Gray)
                }
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = Color.Black)
            }
        }
        HorizontalDivider(thickness = 8.dp, color = Color(0xFFF2F2F2))
        Spacer(Modifier.height(16.dp))

        // Menu Items
        Column (verticalArrangement = Arrangement.spacedBy(10.dp)) {
            AccountMenuItem(
                icon = Icons.Outlined.DateRange,
                label = "Your orders",
                onClick = onOrderClick
            )
            AccountMenuItem(
                icon = Icons.Outlined.ShoppingCart,
                label = "Cart",
                onClick = onOffersClick
            )
            AccountMenuItem(
                icon = Icons.Outlined.FavoriteBorder,
                label = "Wish list",
                onClick = onNotificationsClick
            )
            AccountMenuItem(
                icon = Icons.Outlined.LocalOffer,
                label = "Velora Vouchera",
                onClick = onVeloraVouchersClick
            )
            AccountMenuItem(
                icon = Icons.Outlined.Email,
                label = "Contact us",
                onClick = onHelpClick
            )
            AccountMenuItem(
                icon = Icons.Outlined.Info,
                label = "About app",
                onClick = onAboutClick
            )
        }
    }
}

@Composable
fun AccountMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    AccountMenuItemImpl(label = label, onClick = onClick) {
        Icon(icon, contentDescription = label, tint = Color(0xFF3669C9), modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun AccountMenuItemImpl(
    label: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(modifier = Modifier.width(18.dp))
        Text(label, color = Color.Black, fontSize = 15.sp)
    }
}

@Preview
@Composable
fun AccountScreenPreview() {
    AccountScreen(
        onSettingsClick = {},
        onOrderClick = {},
        onOffersClick = {},
        onNotificationsClick = {},
        onVeloraVouchersClick = {},
        onHelpClick = {},
        onAboutClick = {}
    )
}