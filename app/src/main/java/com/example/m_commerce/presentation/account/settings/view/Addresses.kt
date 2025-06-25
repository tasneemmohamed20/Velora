package com.example.m_commerce.presentation.account.settings.view

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.House
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.m_commerce.R
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.Address
import com.example.m_commerce.domain.entities.AddressType
import com.example.m_commerce.presentation.account.settings.view_model.AddressMapViewModel
import com.example.m_commerce.presentation.utils.components.CustomTopAppBar
import com.example.m_commerce.presentation.utils.routes.ScreensRoute
import com.example.m_commerce.presentation.utils.theme.Primary


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddressesScreen(
    viewModel: AddressMapViewModel,
    onBack: () -> Unit,
    onAddClicked: () -> Unit,
    onAddressClick: (Address) -> Unit,
    navController: NavController,

    ) {
    val addresses by viewModel.addresses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    val sharedPrefsHelper = remember { SharedPreferencesHelper(context) }
    var showGuestDialog by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CustomTopAppBar(
            title = "Addresses",
            onBackClick = onBack,
            actions = {
                Text(
                    text = "Add",
                    color = Primary.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .clickable {
                            if (sharedPrefsHelper.isGuestMode()) {
                                showGuestDialog = true
                            } else {
                                onAddClicked()
                                viewModel.resetForAddMode()
                            }
                        }
                        .padding(horizontal = 8.dp)
                )
                if (showGuestDialog) {
                    AlertDialog(
                        onDismissRequest = { showGuestDialog = false },
                        title = {
                            Text(text = "Sign in Required")
                        },
                        text = {
                            Text("Guests cannot add addresses. Please sign in or create an account to continue.")
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                showGuestDialog = false
                                navController.navigate(ScreensRoute.SignUp)
                            }) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showGuestDialog = false
                            }) {
                                Text("Cancel")
                            }
                        }
                    )
                }

            }
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularWavyProgressIndicator(color = Primary.copy(alpha = 0.7f))
                }
            }

            addresses.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.address_placeholder),
                            contentDescription = "No Address",
                            modifier = Modifier
                                .size(300.dp)
                                .padding(bottom = 16.dp)
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(
                            text = "Tap 'Add' to create your first address",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            else -> {
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
                            onClick = {
                                onAddressClick(addresses[index])
                                viewModel.setupForEditMode(addresses[index])
                            },
                            onDelete = { address ->
                                viewModel.deleteAddress(address)
                            }
                        )
                        if (index < addresses.size - 1) {
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = Color(0xFFF2F2F2),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddressItem(
    address: Address,
    onClick: () -> Unit,
    onDelete: (Address) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Primary.copy(alpha = 0.7f)),
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
                tint = Primary.copy(alpha = 0.7f),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = address.type.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
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
            IconButton(
                onClick = { showDeleteDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete address",
                    tint = Color.Red
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Address") },
            text = {
                Text("Are you sure you want to delete this address? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(address)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}
