
package com.example.m_commerce.presentation.order

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_commerce.presentation.utils.theme.primaryBlue


@Composable
fun OrderScreen(){
    OrderList()
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrderList(){

    LazyColumn {
        items(10){
            OrderCard()
        }
    }
}

@Composable
fun OrderCard(modifier: Modifier = Modifier){
    Card(
        modifier = modifier
            .fillMaxWidth().height(160.dp)
            .padding(12.dp).border(
                color = Color.Gray.copy(alpha = 0.3F),
                width = 1.5.dp,
                shape = RoundedCornerShape(12.dp),
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = { }
    ){
        Column(modifier = Modifier.padding(12.dp)) {
           Row(
               modifier = Modifier.fillMaxWidth().weight(1f),
               verticalAlignment = Alignment.CenterVertically

           ){
               Text(
                   text = "Order#: 6418087313627",
                   color = primaryBlue,
                   style = MaterialTheme.typography.titleMedium,

               )
               Spacer(modifier = Modifier.weight(1f))
               Text(
                   text = "1500EGP",
                   color = Color.Black ,
                   style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
               )
           }
            Text(
                text = "Mon. July 3rd 2025",
                color = Color.Gray.copy(alpha = 0.7F) ,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .border(2.dp, Color.Black, RoundedCornerShape(44    ))
                        .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(44))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Awaiting",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
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