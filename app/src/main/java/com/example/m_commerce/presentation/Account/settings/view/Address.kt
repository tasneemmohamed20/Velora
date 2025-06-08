package com.example.m_commerce.presentation.Account.settings.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.m_commerce.presentation.Account.settings.view_model.AddressesViewModel
import com.example.m_commerce.presentation.utils.components.CustomTopAppBar


enum class AddressType {
    HOME,
    WORK,
    OTHER
}

data class Address(
    val type: AddressType,
    val city: String,
    val building: String,
    val apartment: String,
    val floor: String,
)

private val sampleAddresses = listOf(
    Address(
        type = AddressType.HOME,
        city = "Cairo",
        building = "123",
        apartment = "45A",
        floor = "3rd Floor"
    ),
    Address(
        type = AddressType.WORK,
        city = "Giza",
        building = "456",
        apartment = "12B",
        floor = "5th Floor"
    ),
    Address(
        type = AddressType.OTHER,
        city = "Alexandria",
        building = "789",
        apartment = "34C",
        floor = "2nd Floor"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressesScreen(
    viewModel: AddressesViewModel = viewModel(),
    onBack: () -> Unit,
) {
    val addresses by viewModel.addresses
    val isBottomSheetVisible by viewModel.isBottomSheetVisible
    val density = LocalDensity.current
    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            skipPartiallyExpanded = false,
            initialValue = SheetValue.Hidden,
            confirmValueChange = { true },
            skipHiddenState = false,
            density = density
        )
    )

    LaunchedEffect(isBottomSheetVisible) {
        if (isBottomSheetVisible) {
            sheetState.bottomSheetState.expand()
        } else {
            sheetState.bottomSheetState.hide()
        }
    }

    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetContent = { AddressBottomSheet(viewModel) },
        sheetPeekHeight = 0.dp,
        topBar = {
            CustomTopAppBar(
                title = "Addresses",
                onBackClick = onBack,
                actions = {
                    Text(
                        text = "Add",
                        color = Color.Blue,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .clickable { viewModel.showBottomSheet() }
                            .padding(horizontal = 8.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(addresses) { address ->
                AddressItem(
                    address = address,
                    onClick = { /* Handle address click */ }
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFFF2F2F2),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}


@Composable
fun AddressItem(address: Address, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier.weight(1f)
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(address.type.name)
                        append("  ")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                        append(address.city)
                    }
                },
                color = Color.Unspecified
            )
            Text(
                text = "Building: ${address.building}, Apt: ${address.apartment}, Floor: ${address.floor}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Go",
            tint = Color.Blue
        )
    }
}

@Composable
fun AddressBottomSheet(viewModel: AddressesViewModel) {
    var city by remember { mutableStateOf("") }
    var building by remember { mutableStateOf("") }
    var apartment by remember { mutableStateOf("") }
    var floor by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AddressType.HOME) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Add New Address", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("City") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = building,
            onValueChange = { building = it },
            label = { Text("Building") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = apartment,
            onValueChange = { apartment = it },
            label = { Text("Apartment") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = floor,
            onValueChange = { floor = it },
            label = { Text("Floor") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { viewModel.hideBottomSheet() }
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    viewModel.addAddress(city, building, apartment, floor, selectedType)
                }
            ) {
                Text("Save")
            }
        }
    }
}

