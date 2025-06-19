
package com.example.m_commerce.presentation.order.orders_list

import  android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.m_commerce.ResponseState
import com.example.m_commerce.domain.entities.OrderEntity
import com.example.m_commerce.presentation.utils.Functions.formatShopifyDate
import com.example.m_commerce.presentation.utils.Functions.getOrderStatusColors
import com.example.m_commerce.presentation.utils.Functions.mapOrderStatusSimple
import com.example.m_commerce.presentation.utils.theme.primaryBlue


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderScreen(viewModel: OrderViewModel = hiltViewModel(), onOrderClicked: (OrderEntity) -> Unit){


    LaunchedEffect(Unit){
        viewModel.getOrdersByCustomerId("8437399322843")
    }

    val ordersState by viewModel.ordersList.collectAsStateWithLifecycle()

    AnimatedContent(targetState = ordersState) { ordersState ->
        when(ordersState){
            is ResponseState.Failure ->{

            }
            is ResponseState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            }
            is ResponseState.Success -> {
                val ordersData = ordersState.data as List<OrderEntity>

                OrderList(ordersData, onOrderClicked)
            }
        }
    }


}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderList(orders : List<OrderEntity>, onOrderClicked: (OrderEntity) -> Unit){

    LazyColumn {
        items(orders.size){
            OrderCard(orders[it], onOrderClicked)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderCard(order: OrderEntity, onOrderClicked: (OrderEntity) -> Unit, modifier: Modifier = Modifier){

    val status = mapOrderStatusSimple(order.financialStatus, order.fulfillmentStatus)
    val color = getOrderStatusColors(status)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(12.dp)
            .border(
                color = Color.Gray.copy(alpha = 0.3F),
                width = 1.5.dp,
                shape = RoundedCornerShape(12.dp),
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = {
            onOrderClicked(order) }
    ){
        Column(modifier = Modifier.padding(12.dp)) {
           Row(
               modifier = Modifier
                   .fillMaxWidth()
                   .weight(1f),
               verticalAlignment = Alignment.CenterVertically

           ){
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
               Spacer(modifier = Modifier.weight(1f))
               Text(
                   text = order.totalPrice + order.currency,
                   color = Color.Black ,
                   style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
               )
           }
            Text(
                text = formatShopifyDate(order.createdAt),
                color = Color.Black.copy(alpha = 0.8F) ,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .border(2.dp, color, RoundedCornerShape(44))
                        .background(color.copy(alpha = 0.2f), RoundedCornerShape(44))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Go to Details",
                    tint = Color.Black
                )
            }
        }
    }
}