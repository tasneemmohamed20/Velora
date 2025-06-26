package com.example.m_commerce.presentation.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce.R
import com.example.m_commerce.domain.entities.Customer
import com.example.m_commerce.presentation.utils.ResponseState
import com.example.m_commerce.presentation.utils.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
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

    val customerState by viewModel.customerState.collectAsState()

    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header with ResponseState
        when (customerState) {
            is ResponseState.Loading -> {
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
                            .background(Primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Primary,
                            strokeWidth = 2.dp
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(2f)) {
                        Text(
                            "Loading...",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.egypt),
                                contentDescription = "UAE Flag",
                                modifier = Modifier.size(16.dp),
                                tint = Color.Unspecified
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("EGYPT", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = Color.Black
                        )
                    }
                }
            }

            is ResponseState.Failure -> {
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
                            .background(Color.Red.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "!",
                            color = Color.Red,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(2f)) {
                        Text(
                            "Error loading account",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Red
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.egypt),
                                contentDescription = "UAE Flag",
                                modifier = Modifier.size(16.dp),
                                tint = Color.Unspecified
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("EGYPT", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = Color.Black
                        )
                    }
                }
            }

            is ResponseState.Success -> {
                val successState = customerState as ResponseState.Success
                val customer = successState.data as? Customer
                val customerName = if(customer?.displayName?.isNotEmpty() == true) customer.displayName else  "Guest"
                val avatarLetter =
                    customer?.firstName?.firstOrNull()?.toString()?.uppercase() ?: "G"

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
                            .background(Primary.copy(alpha = 0.2f)),
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
                        Text(
                            "Hi $customerName",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.egypt),
                                contentDescription = "UAE Flag",
                                modifier = Modifier.size(16.dp),
                                tint = Color.Unspecified
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("EGYPT", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = Color.Black
                        )
                    }
                }
            }
        }

        HorizontalDivider(thickness = 8.dp, color = Color(0xFFF2F2F2))
        Spacer(Modifier.height(16.dp))

        // Menu Items
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                label = "Velora Vouchers",
                onClick = onVeloraVouchersClick
            )
            AccountMenuItem(
                icon = Icons.Outlined.Info,
                label = "About app",
                onClick = { showBottomSheet = true }
            )
        }
    }

    // About App Bottom Sheet
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            AboutAppBottomSheetContent()
        }
    }
}

@Composable
fun AboutAppBottomSheetContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "App Logo",
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Velora",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = "Version 1.0.0",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your trusted e-commerce companion for seamless shopping experiences. Discover, shop, and enjoy with Velora.",
            fontSize = 16.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Info Items
        InfoItem(label = "Developer", value = "Velora Team")
        InfoItem(label = "Release Date", value = "2024")
        InfoItem(label = "Category", value = "Shopping & E-commerce")
        InfoItem(label = "Contact", value = "support@velora.com")

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "© 2024 Velora. All rights reserved.",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
fun AccountMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    AccountMenuItemImpl(label = label, onClick = onClick) {
        Icon(
            icon,
            contentDescription = label,
            tint = Primary.copy(alpha = 0.7f),
            modifier = Modifier.size(22.dp)
        )
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