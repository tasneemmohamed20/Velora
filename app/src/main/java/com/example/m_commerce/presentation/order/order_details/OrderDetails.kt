package com.example.m_commerce.presentation.order.order_details

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.m_commerce.domain.entities.Product
import com.example.m_commerce.presentation.products.ProductCard
import com.example.m_commerce.presentation.utils.theme.WhiteSmoke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.text.font.FontWeight
import com.example.m_commerce.domain.entities.OrderEntity
import com.example.m_commerce.presentation.utils.theme.primaryBlue


@Composable
fun OrderDetails(order: OrderEntity){

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize().background(color = Color.White)
            .verticalScroll(scrollState)
    ) {

        Row {
            Text(
                text = "Order ID: ",
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W600)
            )
            Text(
                text = order.name,
                color = primaryBlue,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Order Items",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W600)
            )
            Text(
                text = "${order.lineItems?.fold(0) { acc, product -> acc + product.quantity }} Items",
                color = primaryBlue
            )
        }
        Spacer(Modifier.height(12.dp))

        order.lineItems?.let { ProductsList(it) }

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteSmoke)
        ) {

            Column(modifier = Modifier.padding(16.dp)){
                Text(
                    text = "Address",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W700)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Phone",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = order.phoneNumber,
                        color = Color.Black,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = order.address,
                        color = Color.Black,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

       Column {
           Row(
               modifier = Modifier.fillMaxWidth(),
               horizontalArrangement = Arrangement.SpaceBetween
           ) {
               Text(text = "Subtotal", color = Color.Black.copy(alpha = 0.7f))
               Text(text = order.totalPrice, color = Color.Black.copy(alpha = 0.7f))
           }
           Spacer(Modifier.height(5.dp))
           Row(
               modifier = Modifier.fillMaxWidth(),
               horizontalArrangement = Arrangement.SpaceBetween
           ) {
               Text(text = "Total Tax", color = Color.Black.copy(alpha = 0.7f))
               Text(text = "0.00 EGP", color = Color.Black.copy(alpha = 0.7f))
           }
           Spacer(Modifier.height(10.dp))
           Row(
               modifier = Modifier.fillMaxWidth(),
               horizontalArrangement = Arrangement.SpaceBetween
           ) {
               Text(text = "Total Cost", fontWeight = FontWeight.W700, color = Color.Black)
               Text(text = order.totalPrice, fontWeight = FontWeight.W700, color = Color.Black)
           }

       }
    }
}


@Composable
fun ProductsList(products: List<Product>, modifier: Modifier = Modifier){
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp),
    ) {
        items(products.size){
            ProductCard(
                products[it],
                onProductClick = {  }
            )
        }
    }
}