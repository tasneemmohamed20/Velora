package com.example.m_commerce.presentation.account.settings.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.House
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_commerce.presentation.utils.ResponseState
import com.example.m_commerce.domain.entities.Address
import com.example.m_commerce.domain.entities.AddressType
import com.example.m_commerce.presentation.account.settings.view_model.AddressMapViewModel
import com.example.m_commerce.presentation.utils.components.CustomTopAppBar
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch


@Composable
fun AddressInfo(
    onBack: () -> Unit,
    onSave: (Address) -> Unit,
    viewModel: AddressMapViewModel,
    goToMap: () -> Unit = {}
) {
    val locationState by viewModel.locationState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = locationState) {
            is ResponseState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ResponseState.Failure -> {
                LaunchedEffect(state.err) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = state.err.message ?: "Failed to get location"
                        )
                    }
                }
                SnackbarHost(hostState = snackbarHostState)
            }
            is ResponseState.Success -> {
                val editingAddress by viewModel.editingAddress.collectAsState()
                AddressInfoContent(
                    onBack = onBack,
                    onSave = onSave,
                    viewModel = viewModel,
                    initialAddress = editingAddress,
                    goToMap = goToMap
                )
            }
        }
    }
}


@Composable
private fun AddressInfoContent(
    onBack: () -> Unit,
    onSave: (Address) -> Unit,
    viewModel: AddressMapViewModel,
    initialAddress: Address?,
    goToMap: () -> Unit = {}
){
    val isAddMode by viewModel.isAddMode.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val address by viewModel.address.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val isPhoneValid by viewModel.isPhoneValid.collectAsState()

    var addressType by remember { mutableStateOf(AddressType.HOME) }
    var buildingName by remember { mutableStateOf("") }
    var aptNumber by remember { mutableStateOf("") }
    var floor by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var additionalDirections by remember { mutableStateOf("") }
    var addressLabel by remember { mutableStateOf("") }
    var formattedAddress by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf("EG") }

    val mapLocation = if (isAddMode) {
        selectedLocation ?: currentLocation
    } else {
        currentLocation
    }
    LaunchedEffect(initialAddress, isAddMode) {
        if (!isAddMode && initialAddress != null) {
            addressType = initialAddress.type
            buildingName = initialAddress.building
            aptNumber = initialAddress.apartment
            floor = initialAddress.floor ?: ""
            street = initialAddress.street
            additionalDirections = initialAddress.additionalDirections ?: ""
            addressLabel = initialAddress.addressLabel ?: ""
        } else {
            // Add mode - reset fields
            addressType = AddressType.HOME
            buildingName = ""
            aptNumber = ""
            floor = ""
            street = ""
            additionalDirections = ""
            addressLabel = ""

        }
    }

    formattedAddress = remember(address) {
        address.split(",")
            .filterNot { it.trim().isEmpty() }
            .take(2)
            .joinToString(", ")
            .trim()
    }

    val hasAddressChanged = remember(
        addressType, buildingName, aptNumber, floor, street,
        phoneNumber, additionalDirections, addressLabel, formattedAddress
    ) {
        initialAddress?.let { existing ->
            existing.type != addressType ||
                    existing.building != buildingName ||
                    existing.apartment != aptNumber ||
                    existing.floor != floor ||
                    existing.street != street ||
                    existing.phoneNumber != phoneNumber ||
                    existing.additionalDirections != additionalDirections ||
                    existing.addressLabel != addressLabel ||
                    existing.area != formattedAddress
        } != false
    }

    val isSaveEnabled = buildingName.isNotBlank() &&
            aptNumber.isNotBlank() &&
            street.isNotBlank() &&
            phoneNumber.isNotBlank() &&
            isPhoneValid &&
            hasAddressChanged

    Column {
        // Top bar
        CustomTopAppBar(
            title = "New address",
            onBackClick = onBack,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {

            // Map section
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                mapLocation?.let { location ->
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(location, 15f)
                        },
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            compassEnabled = true,
                            myLocationButtonEnabled = true
                        )
                    ) {
                        Marker(
                            state = MarkerState(position = location),
                            title = "Selected Location",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                        )
                    }
                }
            }


            // Area row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFFF2F2F2), RoundedCornerShape(16.dp))
//                    .padding(8.dp)
            ) {
                Icon(Icons.Default.Place, contentDescription = "Area", tint = Color.Blue)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text("Area", style = MaterialTheme.typography.labelMedium, color = Color.Gray)

                     formattedAddress = remember(address) {
                        address.split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                            .let { parts ->
                                listOfNotNull(
                                    parts.getOrNull(1),
                                    parts.getOrNull(2)
                                ).joinToString(", ")
                            }
                    }

                    Text(
                        text = if (formattedAddress.isNotEmpty()) formattedAddress else "Loading address...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
                TextButton(
                    onClick = {
                        if (isAddMode) {
                            onBack()
                        } else {
                            viewModel.storeFormState(
                                addressType = addressType,
                                buildingName = buildingName,
                                aptNumber = aptNumber,
                                floor = floor,
                                street = street,
                                additionalDirections = additionalDirections,
                                addressLabel = addressLabel
                            )

                        }
                    }
                ) {
                    if (isAddMode) {
                    Text("Change", color = Color.Blue)
                    }else{
                        Text("", color = Color.Blue)
                    }
                }
            }

            // Address type selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AddressType.entries.forEach { type ->
                    OutlinedButton(
                        onClick = { addressType = type },
                        border = BorderStroke(
                            width = if (addressType == type) 2.dp else 1.dp,
                            color = if (addressType == type) Color.Blue else Color(0xFFF2F2F2)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (addressType == type) Color.Blue else Color.White,
                            contentColor = if (addressType == type) Color.White else Color.Black
                        ),
                        shape = RoundedCornerShape(32.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        //                    modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = when (type) {
                                AddressType.APARTMENT -> Icons.Default.Home
                                AddressType.HOME -> Icons.Outlined.House
                                AddressType.OFFICE -> Icons.Default.Business
                            },
                            contentDescription = type.name,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(type.name)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))


            // Form fields
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {

                OutlinedTextField(
                    value = buildingName, onValueChange = { buildingName = it },
                    label = { Text("Building name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.Black)
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = aptNumber, onValueChange = { aptNumber = it },
                        label = { Text("Apt. number") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(color = Color.Black)

                    )
                    OutlinedTextField(
                        value = floor, onValueChange = { floor = it },
                        label = { Text("Floor (optional)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(color = Color.Black)

                    )
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = street, onValueChange = { street = it },
                    label = { Text("Street") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.Black)

                )
                Spacer(Modifier.height(8.dp))

                // Phone number row
                OutlinedTextField(
                    textStyle = TextStyle(color = Color.Black),
                    value = phoneNumber,
                    onValueChange = { newValue ->
                        viewModel.validateAndUpdatePhone(newValue)
                    },
                    label = { Text("Phone number") },
                    leadingIcon = {
                        Text(
                            text = selectedCountry,
                            modifier = Modifier
                                .width(56.dp)
                                .padding(start = 8.dp),
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    placeholder = { Text("Enter 11-digit mobile number") },
                    isError = !isPhoneValid,
                    supportingText = {
                        if (!isPhoneValid) {
                            Text("Please enter a valid Egyptian mobile number")
                        }
                    }
                )

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = additionalDirections, onValueChange = { additionalDirections = it },
                    label = { Text("Additional directions (optional)") },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.Black)

                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = addressLabel, onValueChange = { addressLabel = it },
                    label = { Text("Address label (optional)") },
                    placeholder = { Text("Give this address a label so you can easily choose between them (e.g. Parent's home)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.Black)

                )
                Spacer(Modifier.height(24.dp))

                // Save address button
                Button(
                    onClick = {
                        currentLocation?.let { location ->
                            val newAddress = Address(
                                type = addressType,
                                area = formattedAddress,
                                building = buildingName,
                                apartment = aptNumber,
                                floor = floor,
                                street = street,
                                phoneNumber = phoneNumber,
                                additionalDirections = additionalDirections,
                                addressLabel = addressLabel,
                                latitude = location.latitude,
                                longitude = location.longitude
                            )
                            onSave(newAddress)
                        }
                    },
                    enabled = isSaveEnabled,
                    modifier = Modifier
//                        .padding(horizontal = 8.dp)
                        .fillMaxWidth()
                        .height(56.dp),

                    colors =  ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {

                    Text(
                        if(initialAddress !=null) {
                        "Update address"
                    } else {
                            "Save address"
                        })
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

