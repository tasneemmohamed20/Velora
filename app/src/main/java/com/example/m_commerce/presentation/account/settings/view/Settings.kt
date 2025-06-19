package com.example.m_commerce.presentation.account.settings.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce.presentation.account.settings.view_model.SettingsViewModel
import com.example.m_commerce.presentation.utils.components.CustomTopAppBar


enum class SettingsIcon {
    ARROW_DROP_DOWN,
    ARROW_RIGHT
}

enum class Currency {
    USD,
    EGP,
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onAddressClick: () -> Unit,
    onBackClick: () -> Unit
    ){

    var selectedCurrency by remember {
        mutableStateOf(if (viewModel.getCurrencyPreference()) Currency.USD else Currency.EGP)
    }
    var showDropdown by remember { mutableStateOf(false) }


    Column {
        CustomTopAppBar(
            title = "Settings",
            onBackClick = onBackClick,
        )
        Column(
            modifier = Modifier
                //                .padding(padding)
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
                    onDismissRequest = { showDropdown = false }
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
            SettingsItemRow(label = "Contact Us", onClick = {}, icon = SettingsIcon.ARROW_RIGHT)
            SettingsItemRow(
                label = "About Us",
                value = "",
                onClick = {},
                icon = SettingsIcon.ARROW_RIGHT
            )
            HorizontalDivider(thickness = 8.dp, color = Color(0xFFF2F2F2))
        }
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
                tint = Color.Blue
            )
        }
    }
}
