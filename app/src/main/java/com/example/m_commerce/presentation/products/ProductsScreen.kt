
package com.example.m_commerce.presentation.products

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.m_commerce.ResponseState
import com.example.m_commerce.domain.entities.Product
import com.example.m_commerce.presentation.utils.Functions.formatTitleAndBrand
import com.example.m_commerce.presentation.utils.theme.WhiteSmoke

@Composable
fun ProductsScreen(viewModel: ProductsViewModel = hiltViewModel(), type: String){

    LaunchedEffect(Unit) {
        viewModel.getProductsByType(type)
    }
    val productsState by viewModel.productsList.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        Box(modifier = Modifier.padding(bottom = 16.dp)) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = {
                    Text(
                        text = "Search Product Name",
                        color = Color.Gray.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Black,
                        modifier = Modifier.size(30.dp)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    errorBorderColor = Color.Transparent,
                    focusedContainerColor = WhiteSmoke,
                    unfocusedContainerColor = WhiteSmoke,
                    disabledContainerColor = WhiteSmoke
                )
            )
        }

        when(productsState){
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
                val productsData = (productsState as ResponseState.Success).data as List<Product>
                ProductsList(productsData)
            }
        }

    }

}

@Composable
fun ProductsList(productsState: List<Product>, modifier: Modifier = Modifier){

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .background(color = Color(0xfff9f9f9))
            .padding(top = 16.dp, start = 8.dp, end = 8.dp)
    ) {
        items(productsState.size){
            ProductCard(productsState[it])
        }
    }
}

@Composable
fun ProductCard(productDetails: Product, modifier: Modifier = Modifier) {

    val (brand, productName) = formatTitleAndBrand(productDetails.title)
    Card(
        modifier = modifier
            .wrapContentSize()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(modifier = Modifier.height(180.dp)) {
                SubcomposeAsyncImage(
                    model = productDetails.image,
                    loading = {
                        CircularProgressIndicator(modifier = Modifier.size(25.dp))
                    },
                    error = {
                    },
                    contentDescription = "Network Image with Coil (Sub compose)",
                    modifier = Modifier.size(160.dp),
                    contentScale = ContentScale.Inside
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
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

            Box(modifier = Modifier
                .padding(10.dp)
                .height(80.dp)) {
                Column {
                    Text(
                        text = brand,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = productName,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${productDetails.price.minVariantPrice.amount}${productDetails.price.minVariantPrice.currencyCode}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Red,
                    )
                }
            }
        }
    }
}


