package com.example.m_commerce.presentation.payment.checkout

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce.domain.entities.payment.OrderItem
import com.example.m_commerce.presentation.checkout.CheckoutViewModel
import com.example.m_commerce.presentation.utils.components.CustomTopAppBar
//import com.example.m_commerce.presentation.utils.components.ErrorAlertDialog
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

enum class PaymentMethod {
    CASH,
    PAYMOB
}

@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    totalPrice : Double,
    subtotal: Double,
    estimatedFee: Double,
    itemsCount: Int,
    items: List<OrderItem>,
    onConfirmOrder: (PaymentMethod, List<OrderItem>, Int) -> Unit,
    checkoutViewModel: CheckoutViewModel = hiltViewModel(),
    onOrderCompleted: () -> Unit
) {
    // Check if total price exceeds cash on delivery limit
    val exceedsCashLimit = totalPrice > 5000.0

    var selectedPaymentMethod by remember {
        mutableStateOf(if (exceedsCashLimit) PaymentMethod.PAYMOB else PaymentMethod.CASH)
    }
    var showPriceExceedsError by remember {
        mutableStateOf(false)
    }
    val location by checkoutViewModel.selectedLocation.collectAsState()
    val selectedAddress by checkoutViewModel.selectedAddress.collectAsState()

    val voucherText by checkoutViewModel.voucherText.collectAsState()
    val voucherError by checkoutViewModel.voucherError.collectAsState()
    val appliedDiscount by checkoutViewModel.appliedDiscount.collectAsState()
    val isApplyingVoucher by checkoutViewModel.isApplyingVoucher.collectAsState()
    val totalPriceAfterDiscount = totalPrice - appliedDiscount
    Log.d("CheckoutScreen", "CheckoutScreen: $location")
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Checkout",
                onBackClick = onBack,
            )
        },
        bottomBar = {
            ConfirmOrderBottomBar(
                onConfirmOrder = {
                    onConfirmOrder(selectedPaymentMethod, items, (totalPriceAfterDiscount * 100).toInt())
                    if(selectedPaymentMethod == PaymentMethod.CASH){
                            checkoutViewModel.completeDraftOrder()
                    }
                },
                isEnabled = !showPriceExceedsError
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            val location2 = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
            MapAndAddressSection(location2,
                address = selectedAddress.toString()
            )
            Spacer(Modifier.height(16.dp))

            VoucherSection(
                voucherText = voucherText,
                voucherError = voucherError,
                appliedDiscount = appliedDiscount,
                isApplyingVoucher = isApplyingVoucher,
                onVoucherTextChange = checkoutViewModel::updateVoucherText,
                onApplyVoucher = checkoutViewModel::applyVoucher
            )
            Spacer(Modifier.height(16.dp))

            PayWithSection(
                selectedMethod = selectedPaymentMethod,
                onMethodSelected = { method ->
                    when {
                        method == PaymentMethod.CASH && exceedsCashLimit -> {
                            showPriceExceedsError = true
                            return@PayWithSection
                        }

                        method == PaymentMethod.PAYMOB -> {
                            selectedPaymentMethod = method
                            showPriceExceedsError = false
                            return@PayWithSection
                        }

                        else -> {
                            selectedPaymentMethod = method
                            showPriceExceedsError = false
                        }
                    }
                },
                exceedsCashLimit = exceedsCashLimit,
                showPriceExceedsError = showPriceExceedsError
            )
            Spacer(Modifier.height(8.dp))

            PaymentSummarySection(
                subtotal = subtotal,
                estimatedFee = estimatedFee,
                itemsCount = itemsCount,
                totalPrice = totalPrice,
                appliedDiscount = appliedDiscount
            )
            Spacer(Modifier.height(24.dp))

            if(checkoutViewModel.showErrorDialog.value){
//                ErrorAlertDialog(message = "Failed to complete order. Please try again.", onDismiss = {checkoutViewModel.toggleErrorAlert()})
            }

            if (checkoutViewModel.showSuccessDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        checkoutViewModel.toggleSuccessAlert()
                        onOrderCompleted()
                    },
                    title = {
                        Text("🎉 Order Confirmed!", style = MaterialTheme.typography.titleLarge)
                    },
                    text = {
                        Text(
                            "Your order has been placed successfully. Thank you!",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            checkoutViewModel.toggleSuccessAlert()
                            onOrderCompleted()
                        }) {
                            Text("OK")
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF4CAF50), // Green
                        )
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}

@Composable
private fun MapAndAddressSection(location: LatLng, address: String) {
    if (location.latitude != 0.0 || location.longitude != 0.0) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(location, 15f)
                    Log.d("CheckoutScreen", "Camera position set to $location")
                },
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    compassEnabled = true,
                    myLocationButtonEnabled = true,
                )
            ) {
                Marker(
                    state = MarkerState(position = location),
                    title = "Selected Location",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Gray.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Loading location...",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFF2F2F2), RoundedCornerShape(16.dp))
            .padding(8.dp)
    ) {
        Icon(Icons.Default.Place, contentDescription = "Area", tint = Color.Blue)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text("Area", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(
                text = address,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun VoucherSection(
    voucherText: String,
    voucherError: String?,
    appliedDiscount: Double,
    isApplyingVoucher: Boolean,
    onVoucherTextChange: (String) -> Unit,
    onApplyVoucher: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Save on your order",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Voucher icon
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Voucher",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Text field without border
            BasicTextField(
                value = voucherText,
                onValueChange = onVoucherTextChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                decorationBox = { innerTextField ->
                    if (voucherText.isEmpty()) {
                        Text(
                            text = "Enter voucher code",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            if (isApplyingVoucher) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color(0xFFFF5722),
                    strokeWidth = 2.dp
                )
            } else {
                TextButton(
                    onClick = onApplyVoucher,
                    enabled = voucherText.isNotBlank()
                ) {
                    Text(
                        text = "Submit",
                        color = Color.Blue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (voucherError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = voucherError,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )
        } else if (appliedDiscount > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Discount applied: EGP %.2f".format(appliedDiscount),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Green,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun PayWithSection(
    selectedMethod: PaymentMethod,
    onMethodSelected: (PaymentMethod) -> Unit,
    exceedsCashLimit: Boolean,
    showPriceExceedsError: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Pay with",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.Black
        )
        Spacer(Modifier.height(8.dp))
        PaymentOptionCard(
            title = "Cash on delivery",
            icon = Icons.Default.Money,
            isSelected = selectedMethod == PaymentMethod.CASH,
            onClick = { onMethodSelected(PaymentMethod.CASH) },
            isEnabled = !exceedsCashLimit
        )
        Spacer(Modifier.height(8.dp))
        PaymentOptionCard(
            title = "Paymob",
            icon = Icons.Default.CreditCard,
            isSelected = selectedMethod == PaymentMethod.PAYMOB,
            onClick = { onMethodSelected(PaymentMethod.PAYMOB) },
            isEnabled = true
        )
        Spacer(Modifier.height(8.dp))
        if (showPriceExceedsError) {
            Text(
                text = "Total price exceeds cash on delivery limit of 5000 EGP",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Red
            )
        } else {
            Text(
                text = "Cash on delivery limit is 5000 EGP",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Red,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun PaymentOptionCard(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    isEnabled: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) Color.Blue else if (isEnabled) Color.LightGray else Color.Gray
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = isEnabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isEnabled) Color.Blue else Color.Gray
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isEnabled) Color.Black else Color.Gray
        )
        Spacer(Modifier.weight(1f))
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            enabled = isEnabled,
            colors = RadioButtonDefaults.colors(
                selectedColor = if (isEnabled) Color.Blue else Color.Gray,
                unselectedColor = if (isEnabled) Color.Gray else Color.LightGray
            )
        )
    }
}

@Composable
private fun PaymentSummarySection(
    subtotal: Double,
    estimatedFee: Double,
    itemsCount: Int,
    totalPrice: Double,
    appliedDiscount: Double
) {
    val itemsLabel = if (itemsCount > 1) "items" else "item"
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Payment summary",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.Black
        )
        Spacer(Modifier.height(8.dp))
        SummaryRow("Subtotal - $itemsCount $itemsLabel", "EGP %.2f".format(subtotal))
        if (appliedDiscount > 0) {
            SummaryRow("Discount", "-EGP %.2f".format(appliedDiscount))
        }
        SummaryRow("Shipping fee", "FREE")
        SummaryRow("Estimated fee", "EGP %.2f".format(estimatedFee))
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        SummaryRow("Total price", "EGP %.2f".format(totalPrice - appliedDiscount), isBold = true)
    }
}

@Composable
private fun SummaryRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            fontSize = 16.sp,
            color = Color.Black
        )
        Text(
            value,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Composable
private fun ConfirmOrderBottomBar(
    onConfirmOrder: () -> Unit,
    isEnabled: Boolean = true
) {
    var isLoading by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = Color.White,
    ) {
        Button(
            onClick = {
                isLoading = true
                onConfirmOrder()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White
            ),
            enabled = isEnabled && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "Place Order",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
