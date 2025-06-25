package com.example.m_commerce.presentation.cart

import android.R
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.presentation.utils.ResponseState
import com.example.m_commerce.domain.entities.DraftOrder
import com.example.m_commerce.presentation.utils.components.CustomTopAppBar
import com.example.m_commerce.presentation.utils.theme.Primary

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CartScreen(
    onBack: () -> Unit,
    onCheckout:(order:DraftOrder, total : Int, subtotal: Double?, estimatedFee: Double?, itemsCount: Int?) -> Unit,
    viewModel: CartViewModel = hiltViewModel(),
    modifier: Modifier = Modifier.background(Color.White)
) {
    var voucherCode by remember { mutableStateOf("") }
    val cartState by viewModel.cartState.collectAsState()

    var subtotal : Double? = null
    var estimatedFee: Double? = null
    var itemsCount: Int? = null
    var totalPrice: Double? = null
    val context = LocalContext.current
    val sharedPreferencesHelper = remember { SharedPreferencesHelper(context) }
    val isAuthenticated = remember { sharedPreferencesHelper.isUserAuthenticated() }

    val removeItemRequest = viewModel.removeItemRequest
    var itemToRemove by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        removeItemRequest.collect { variantId ->
            itemToRemove = variantId
        }
    }

    if (itemToRemove != null) {
        AlertDialog(
            onDismissRequest = { itemToRemove = null },
            title = { Text("Remove Item") },
            text = { Text("Are you sure you want to remove this item from your cart?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        Log.d("CartScreen", "Removing item: $itemToRemove")
                        if (itemToRemove == "ALL") {
                            Log.d("CartScreen", "Removing all items")
                            viewModel.deleteDraftOrder()
                        } else {
                            Log.d("CartScreen", "Removing item: $itemToRemove")
                            viewModel.removeItem(itemToRemove!!)
                        }
                        itemToRemove = null
                    }
                ) { Text("Remove") }
            },
            dismissButton = {
                TextButton(onClick = { itemToRemove = null }) { Text("Cancel") }
            }
        )
    }

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
            Column(
                Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                CartHeader(onBack)
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Empty Cart",
                            modifier = Modifier
                                .size(100.dp)
                                .padding(8.dp),
                            colorFilter = ColorFilter.tint(Primary)
                        )
                        Text(
                            text = if (isAuthenticated) "Your cart is empty" else "Please log in to view your cart",
                            textAlign = TextAlign.Center,
                            color = Primary
                        )

                    }
                }
            }
        }

        is ResponseState.Success -> {
            val draftOrder = (cartState as ResponseState.Success).data
            Column(
                Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                CartHeader(onBack)

                LazyColumn(modifier = Modifier.weight(1f)) {
                    when (draftOrder) {
                        is DraftOrder -> {
                            val nodes = draftOrder.lineItems?.nodes ?: emptyList()
                            subtotal = draftOrder.subtotalPrice
                            totalPrice = draftOrder.totalPrice
                            estimatedFee = draftOrder.totalTax ?: 0.0
                            itemsCount = draftOrder.totalQuantityOfLineItems
                            Log.d("CartScreen", "Draft order items: ${nodes.size}")
                            Log.d("CartScreen", "Cart items: $nodes")
                            if (nodes.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Your cart is empty",
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                items(nodes) { item ->
                                    CartItemRow(
                                        cartItem = CartItem(
                                            name = item.title ?: item.name ?: "Unknown Item",
                                            imageUrl = item.image?.url.toString(),
                                            quantity = item.quantity ?: 1,
                                            price = item.originalUnitPrice ?: 0.0,
                                            id = item.variantId.toString(),
                                            size = item.product?.variants?.find { variant ->
                                                variant.id == item.variantId
                                            }?.selectedOptions?.firstOrNull { it?.name == "Size" }?.value
                                                ?: "",
                                            color = item.product?.variants?.find { variant ->
                                                variant.id == item.variantId
                                            }?.selectedOptions?.firstOrNull { it?.name == "Color" }?.value
                                                ?: ""
                                        ),
                                        onQuantityChange = { newQty ->
                                            if (newQty >= 1) {
                                                item.id?.let { itemId ->
                                                    viewModel.updateQuantity(itemId, newQty)
                                                }
                                            } else {

                                                if ((itemsCount ?: 0) > 1)  {
                                                item.id?.let { itemId ->
                                                    viewModel.requestRemoveItem(item.id)
                                                }
                                                }else {
                                                itemToRemove = "ALL"
                                            }
                                            }
                                        },
                                        onRemove = {
                                            if (((itemsCount ?: 0) > 1))  {
                                                if(item.quantity == (itemsCount ?: 0)){
                                                    itemToRemove = "ALL"
                                                }else {
                                                item.id?.let { itemId ->
                                                    viewModel.requestRemoveItem(item.id)
                                                }
                                                }
                                            }
                                            else {
//                                                viewModel.deleteDraftOrder()
                                                itemToRemove = "ALL"
                                                Log.d("TAG", "onRemove: ${itemToRemove}")
                                            }
                                            Log.d("TAG", "onRemove: ${itemToRemove}")
                                        }
                                    )
                                    HorizontalDivider(modifier = Modifier.padding(8.dp))
                                }
                            }
                        }
                    }

                    item { Spacer(Modifier.height(18.dp)) }
                    item {
                        PaymentSummarySection(
                            subtotal = subtotal ?: 0.0,
                            estimatedFee = estimatedFee ?: 0.0,
                            itemsCount = itemsCount ?: 0,
                            totalPrice = totalPrice ?: 0.0,
                        )
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
                BottomButtons(
                    onCheckout = {
                        when (val state = cartState) {
                            is ResponseState.Success -> {
                                val order = state.data as DraftOrder
                                val totalAmountCents = (order.totalPrice?.times(100))?.toInt() ?: 0
                                val subtotal = order.subtotalPrice
                                val estimatedFee = order.totalTax
                                val itemsCount = order.totalQuantityOfLineItems
                                onCheckout(order, totalAmountCents, subtotal, estimatedFee, itemsCount)
                            }
                            else -> {}
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun CartHeader(onBack: () -> Unit) {
    CustomTopAppBar(
        title = "Cart",
        onBackClick = onBack,
    )
    Spacer(Modifier.height(10.dp))
}

@Composable
fun PaymentSummarySection(subtotal: Double, estimatedFee: Double, itemsCount: Int, totalPrice: Double) {
    var item : String? = null
    item = if (itemsCount > 1 ){
        "items"
    } else {
        "item"
    }
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
        SummaryRow("Subtotal - $itemsCount items", "EGP %.2f".format(subtotal))
        SummaryRow("Shipping fee", "FREE")
        SummaryRow("Estimated fee", "EGP %.2f".format(estimatedFee))
        HorizontalDivider(Modifier.padding(8.dp))
        SummaryRow("Total price", "EGP %.2f".format(totalPrice))   }
}

@Composable
fun BottomButtons(onCheckout: () -> Unit, isEnabled: Boolean = true) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        shadowElevation = 16.dp,
        tonalElevation = 8.dp,
        color = Color.White,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                ),
                enabled = isEnabled
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


data class CartItem(
    val name: String,
    val imageUrl: String,
    var quantity: Int,
    val price: Double,
    val size: String ,
    val color: String,
    val id: String
)
