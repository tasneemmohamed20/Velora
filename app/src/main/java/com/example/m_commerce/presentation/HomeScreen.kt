package com.example.m_commerce.presentation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.m_commerce.R


@Preview(showBackground = true,
         showSystemUi = true)
@Composable
fun HomeScreen(){
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(start = 20.dp, top = 10.dp, end = 20.dp)
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color.White, shape = RoundedCornerShape(12.dp))
                .background(color = Color.Red, shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ){
            Image(
                painter = painterResource(id = R.drawable.coupon),
                contentDescription = "coupon",
                modifier = Modifier.size(150.dp)
            )
        }
        Spacer(Modifier.height(15.dp))
        Categories()
        Spacer(Modifier.height(15.dp))
        Brands()
    }
}

@Preview
@Composable
fun Categories(modifier: Modifier = Modifier){
    Text(
        text = "Categories",
        style = MaterialTheme.typography.titleLarge
    )
    Spacer(Modifier.height(10.dp))
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ){
        CategoryItem("Women", id = R.drawable.women)
        CategoryItem("Men", id = R.drawable.men)
        CategoryItem("Kid", id = R.drawable.kid)
        CategoryItem("Sale", id = R.drawable.sale)
    }
}

@Composable
fun CategoryItem(type: String, @DrawableRes id: Int, modifier: Modifier = Modifier){

    val boxColor: Color = when(type){
        "Women" -> Color.Green.copy(alpha = 0.2F)
        "Men" -> Color.Red.copy(alpha = 0.2F)
        "Kid" -> Color.Yellow.copy(alpha = 0.2F)
        else -> Color.Green.copy(alpha = 0.2F)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(color = boxColor, shape = RoundedCornerShape(12.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ){
            Image(
                painter = painterResource(id = id),
                contentDescription = type,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(Modifier.height(5.dp))
        Text(
            text = type,
            color = Color.Black,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Preview
@Composable
fun Brands(){
    Text(
        text = "Brands",
        style = MaterialTheme.typography.titleLarge
    )
    Spacer(Modifier.height(10.dp))
    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(280.dp)
    ) {
        items(16){
            BrandItem()
        }
    }
}


@Composable
fun BrandItem(modifier: Modifier = Modifier){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                .border(
                    color = Color.Gray.copy(alpha = 0.3F),
                    width = 2.dp,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ){
            Image(
                painter = painterResource(id = R.drawable.adidas),
                contentDescription = "type",
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(Modifier.height(5.dp))
        Text(
            text = "type",
            color = Color.Black,
            style = MaterialTheme.typography.titleMedium
        )
    }
}