package com.example.m_commerce.presentation.cart

import android.R
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.m_commerce.ResponseState
import com.example.m_commerce.domain.entities.DraftOrder
import com.example.m_commerce.presentation.utils.components.CustomTopAppBar
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialShapes
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview


data class CartItem(
    val name: String,
    val imageUrl: String,
    var quantity: Int,
    val price: Double,
    val size: String ,
    val color: String,
    val id: String
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CartHeader(onBack: () -> Unit) {
    CustomTopAppBar(
        title = "Cart",
        onBackClick = onBack,
    )
    Spacer(Modifier.height(10.dp))
}

@Composable
fun SpecialRequestSection(specialRequest: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Special request",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.ChatBubbleOutline,
                contentDescription = null,
                tint = Color.Black
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = "Any special requests?",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = "Anything else we need to know?",
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun VoucherSection(voucherCode: String, onVoucherChange: (String) -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Save on your order",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.Black
        )
        Spacer(Modifier.height(6.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFECECEC), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_menu_save),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            TextField(
                value = voucherCode,
                onValueChange = onVoucherChange,
                placeholder = { Text("Enter voucher code", color = Color.Gray) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { /* Submit voucher */ },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
            ) {
                Text("Submit", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PaymentSummarySection(subtotal: Double, deliveryFee: Double, serviceFee: Double, proDiscount: Double) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Payment summary",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.Black
        )
        Spacer(Modifier.height(8.dp))
        SummaryRow("Subtotal", "EGP %.2f".format(subtotal))
        SummaryRow("Delivery fee", "EGP %.2f".format(deliveryFee), info = true)
        ProOfferRow(proDiscount)
        SummaryRow("Service fee", "EGP %.2f".format(serviceFee), info = true)
        HorizontalDivider(Modifier.padding(8.dp))
//        TotalRow(subtotal + deliveryFee + serviceFee)
    }
}

@Composable
fun ProOfferRow(proDiscount: Double) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF1E8FC))
            .padding(vertical = 8.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = Color(0xFF7B36F2),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                "pro",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            "Save EGP %.2f on this order".format(proDiscount),
            color = Color(0xFF7B36F2),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.weight(1f))
        Text(
            "Try free",
            color = Color(0xFF7B36F2),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun BottomButtons(onAddItems: () -> Unit, onCheckout: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedButton(
            onClick = onAddItems,
            shape = RoundedCornerShape(32.dp),
            border = BorderStroke(1.dp, Color.Blue),
            modifier = Modifier
                .weight(1f)
                .height(54.dp)
        ) {
            Text(
                "Add items",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        Spacer(Modifier.width(16.dp))
        Button(
            onClick = onCheckout,
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            modifier = Modifier
                .weight(1f)
                .height(54.dp)
        ) {
            Text(
                "Checkout",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CartScreen(
    onBack: () -> Unit,
    viewModel: CartViewModel = hiltViewModel(),
    modifier: Modifier = Modifier.background(Color.White)
) {
    var specialRequest by remember { mutableStateOf("") }
    var voucherCode by remember { mutableStateOf("") }
    val cartState by viewModel.cartState.collectAsState()

    when (cartState) {
        is ResponseState.Loading -> {
            CircularWavyProgressIndicator(
                stroke = Stroke(width = 4.0f, cap = StrokeCap.Round),
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                color = Color.Blue,
            )
        }
        is ResponseState.Failure -> {
            val error = (cartState as ResponseState.Failure).err
            Log.e("CartScreen", "Error loading cart items", error)
        }
        is ResponseState.Success -> {
            val draftOrder = (cartState as ResponseState.Success).data
            Column(
                Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                CartHeader(onBack)

                LazyColumn {
                    when (val order = draftOrder) {
                        is DraftOrder -> {
                            val nodes = order.lineItems?.nodes ?: emptyList()
                            Log.d("CartScreen", "Cart items: $nodes")
                            if (nodes.isEmpty()) {
                                item {
                                    Text(
                                        text = "Your cart is empty",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                items(nodes) { item ->

                                    CartItemRow(
                                        cartItem = CartItem(
                                            name = item.title ?: item.name ?: "Unknown Item",
                                            imageUrl = item.image?.url.toString(),
                                            quantity = item.quantity ?: 1,
                                            price = item.originalUnitPrice?: 0.0,
                                            id = item.variantId.toString(),
                                            size = item.product?.variants?.find { variant ->
                                                variant.id == item.variantId
                                            }?.selectedOptions?.firstOrNull { it?.name == "Size" }?.value ?: "",
                                            color = item.product?.variants?.find { variant ->
                                                variant.id == item.variantId
                                            }?.selectedOptions?.firstOrNull { it?.name == "Color" }?.value ?: ""
                                        ),
                                        onQuantityChange = { newQty ->
                                            item.id?.let { itemId ->
                                                viewModel.updateQuantity(itemId, newQty)
                                            }
                                        },
                                        onRemove = {
                                            item.id?.let { itemId ->
                                                viewModel.removeItem(itemId)
                                            }
                                        }
                                    )
                                    HorizontalDivider(modifier = Modifier.padding(8.dp))
                                }
                            }
                        }
                    }

                    item { SpecialRequestSection(specialRequest) }
                    item { Spacer(Modifier.height(18.dp)) }
                    item { VoucherSection(voucherCode) { voucherCode = it } }
                    item { Spacer(Modifier.height(18.dp)) }
                    item {
                        PaymentSummarySection(
                            subtotal = 0.0,
                            deliveryFee = 16.99,
                            serviceFee = 5.80,
                            proDiscount = 16.99
                        )
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                    item {
                        BottomButtons(
                            onAddItems = { /* Handle add items */ },
                            onCheckout = { /* Handle checkout */ }
                        )
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, info: Boolean = false) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                label,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Color.Black
            )
            if (info) {
                Spacer(Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu_info_details),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
        Text(
            value,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Composable
fun CartItemRow(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {

    var quantityTemp :Int? = null

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.Red
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    space = 10.dp,
                    alignment = Alignment.CenterVertically
                ) ,

            ) {
                Image(
                    painter = rememberAsyncImagePainter(cartItem.imageUrl),
                    contentDescription = cartItem.name,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .height(80.dp)
                        .width(100.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            width = 1.dp,
                            color = Color.Blue,
                            shape = RoundedCornerShape(8.dp)
                        )
                )
                Text(
                    text = cartItem.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Size: ${cartItem.size} - Color: ${cartItem.color}",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Normal
                )

                Text(
                    text = "EGP ${"%.2f".format(cartItem.price)}",
                    fontSize = 15.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(12.dp))

            // Quantity section
            Box (modifier = Modifier
                .align (
                    Alignment.CenterVertically
                )
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFF5F5F5), RoundedCornerShape(24.dp))
                        .height(40.dp)

                ) {
                    IconButton(
                        onClick = {
                            onQuantityChange(cartItem.quantity - 1)
                                  },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text(
                            text = "-",
                            color = Color.Blue,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    Text(
                        "${cartItem.quantity}",
                        modifier = Modifier.width(20.dp),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    IconButton(
                        onClick = { onQuantityChange(cartItem.quantity + 1) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = Color.Blue
                        )
                    }
                }
            }
        }

    }
}

//@Preview
//@Composable
//fun CartItemRowPreview() {
//    CartItemRow(
//        cartItem = CartItem(
//            name = "Sample Item",
//            imageUrl = R.drawable.ic_menu_info_details.toString(),
//            quantity = 2,
//            price = 99.99,
//            size = "M",
//            color = "Red",
//            id = "12345"
//        ),
//        onDelete = {},
//        onQuantityChange = {},
//        onRemove = {}
//    )
//
//}


