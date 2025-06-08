
package com.example.m_commerce.presentation.products

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.m_commerce.R

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductsScreen(){
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 16.dp)
    ) {
        Box(modifier = Modifier.padding(bottom = 16.dp)){
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = {
                    Text(text = "Search Product Name")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Icon(imageVector = Icons.Default.Search,
                        contentDescription = "Add to favorites",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp
                        )
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xfff9f9f9),
                    unfocusedContainerColor = Color(0xfff9f9f9),
                    disabledContainerColor = Color(0xfff9f9f9)
                )
            )
        }
        ProductsList()
    }


}

@Composable
fun ProductsList(modifier: Modifier = Modifier){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.background(color = Color(0xfff9f9f9)).padding(top = 16.dp)
    ) {
        items(16){
            ProductCard()
        }
    }
}

@Composable
fun ProductCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .wrapContentSize().padding(bottom = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(modifier = Modifier.height(180.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.adidas),
                    contentDescription = "type",
                    modifier = Modifier
                        .size(180.dp)
                        .align(Alignment.Center)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .size(40.dp)
                        .background(
                            color = Color.White,
                            shape = CircleShape
                        )
                        .clickable { }
                        .shadow(2.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Add to favorites",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Box(modifier = Modifier.padding(10.dp)) {
                Column {
                    Text(
                        text = "TMA-2 HD Wireless",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black,
                    )
                    Text(
                        text = "Adidas",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black,
                    )
                    Text(
                        text = "1500EGP",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Red,
                    )
                }
            }
        }
    }
}



@Composable
fun ProductCardPreview() {
    MaterialTheme {
        ProductCard()
    }
}
