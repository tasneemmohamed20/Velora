package com.example.m_commerce.presentation.account.settings.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce.R
import com.example.m_commerce.presentation.account.settings.view_model.SettingsViewModel
import com.example.m_commerce.presentation.utils.components.CustomTopAppBar
import com.example.m_commerce.presentation.utils.theme.Primary

enum class SettingsIcon {
    ARROW_DROP_DOWN,
    ARROW_RIGHT
}

enum class Currency {
    USD,
    EGP,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onAddressClick: () -> Unit,
    onBackClick: () -> Unit,
    onLogoutClicked: () -> Unit
) {

    var selectedCurrency by remember {
        mutableStateOf(if (viewModel.getCurrencyPreference()) Currency.USD else Currency.EGP)
    }
    var showDropdown by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val isGuestMode = viewModel.getCurrentUserMode() == "Guest"

    Column {
        CustomTopAppBar(
            title = "Settings",
            onBackClick = onBackClick,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            HorizontalDivider(thickness = 8.dp, color = Color(0xFFF2F2F2))
            SettingsItemRow(
                label = "Addresses",
                onClick = onAddressClick,
                icon = SettingsIcon.ARROW_RIGHT
            )
            HorizontalDivider(thickness = 8.dp, color = Color(0xFFF2F2F2))

            Box {
                SettingsItemRow(
                    label = "Currency",
                    value = viewModel.currencyExchange.collectAsState(initial = null).value?.rates?.EGP
                        ?: selectedCurrency.name,
                    onClick = { showDropdown = true },
                    icon = SettingsIcon.ARROW_DROP_DOWN
                )
                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false },
                    offset = DpOffset(LocalConfiguration.current.screenWidthDp.dp, 0.dp)
                ) {
                    Currency.entries.forEach { currency ->
                        DropdownMenuItem(
                            text = { Text(currency.name) },
                            onClick = {
                                selectedCurrency = currency
                                showDropdown = false
                                viewModel.setCurrencyPreference(currency == Currency.USD)
                                if (currency == Currency.USD) {
                                    viewModel.getCurrencyExchange()
                                }
                            }
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 8.dp, color = Color(0xFFF2F2F2))
            SettingsItemRow(
                label = "About Us",
                value = "",
                onClick = { showBottomSheet = true },
                icon = SettingsIcon.ARROW_RIGHT
            )
            HorizontalDivider(thickness = 8.dp, color = Color(0xFFF2F2F2))
            SettingsItemRow(
                label = if (isGuestMode) "Log in" else "Log out",
                value = "",
                onClick = {
                    onLogoutClicked()
                },
                icon = SettingsIcon.ARROW_RIGHT
            )
        }
    }

    // About Us Bottom Sheet
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            AboutUsBottomSheetContent()
        }
    }
}

@Composable
private fun AboutUsBottomSheetContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Icon/Logo placeholder
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
        InfoItem(label = "Release Date", value = "2025")
        InfoItem(label = "Category", value = "Shopping & E-commerce")
        InfoItem(label = "Contact", value = "support@velora.com")

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "© 2025 Velora. All rights reserved.",
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
fun SettingsItemRow(
    label: String,
    value: String? = "",
    icon: SettingsIcon,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 16.sp, color = Color.Black)

        Row(verticalAlignment = Alignment.CenterVertically) {

            if (value != null) {
                Text(
                    text = value,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }

            Icon(
                imageVector = when (icon) {
                    SettingsIcon.ARROW_DROP_DOWN -> Icons.Default.ArrowDropDown
                    SettingsIcon.ARROW_RIGHT -> Icons.AutoMirrored.Filled.KeyboardArrowRight
                },
                contentDescription = null,
                tint = Primary.copy(alpha = 0.7f)
            )
        }
    }
}
