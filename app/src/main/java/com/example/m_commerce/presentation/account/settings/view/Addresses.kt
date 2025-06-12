package com.example.m_commerce.presentation.account.settings.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.House
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_commerce.domain.entities.Address
import com.example.m_commerce.domain.entities.AddressType
import com.example.m_commerce.presentation.account.settings.view_model.AddressMapViewModel
import com.example.m_commerce.presentation.utils.components.CustomTopAppBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressesScreen(
    viewModel: AddressMapViewModel,
    onBack: () -> Unit,
    onAddClicked: () -> Unit,
    onAddressClick: (Address) -> Unit
) {
    val addresses by viewModel.addresses.collectAsState()
    val areAddressesFull by viewModel.areAddressesFull.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val showDialog = remember { mutableStateOf(false) }

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
                        .clickable {
                            if (areAddressesFull) {
                                showDialog.value = true
                            } else {
                                onAddClicked()
                            }
                        }
                        .padding(horizontal = 8.dp)
                )
            }
        )

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Maximum Addresses Reached") },
                text = { Text("You can only add up to 2 addresses. Please remove an existing address to add a new one.") },
                confirmButton = {
                    TextButton(onClick = { showDialog.value = false }) {
                        Text("OK")
                    }
                }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                count = addresses.size,
                key = { index -> index }
            ) { index ->
                AddressItem(
                    address = addresses[index],
                    onClick = { onAddressClick(addresses[index]) }
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Color.Blue),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick

    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (address.type) {
                    AddressType.HOME -> Icons.Outlined.House
                    AddressType.APARTMENT -> Icons.Filled.Home
                    AddressType.OFFICE -> Icons.Filled.Business
                },
                contentDescription = address.type.name,
                tint = Color.Blue,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = address.type.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Blue
                )
                Text(
                    text = address.area,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Text(
                    text = "${address.street}, ${address.building}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
