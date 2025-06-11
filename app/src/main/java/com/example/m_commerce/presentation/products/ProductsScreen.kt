
package com.example.m_commerce.presentation.products

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

        FilterChips(modifier = Modifier.padding(bottom = 16.dp), viewModel)

        AnimatedContent(targetState = productsState) { productsState ->
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

                    ProductsList(productsData, Modifier.weight(2F))
                }
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
            .background(color = WhiteSmoke)
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
            .width(160.dp)
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(modifier = Modifier.height(150.dp)) {
                SubcomposeAsyncImage(
                    model = productDetails.image,
                    loading = {
                        CircularProgressIndicator(modifier = Modifier.size(25.dp))
                    },
                    error = {
                    },
                    contentDescription = "Network Image with Coil (Sub compose)",
                    modifier = Modifier.size(140.dp),
                    contentScale = ContentScale.Inside
                )
//                Box(
//                    modifier = Modifier
//                        .align(Alignment.TopEnd)
//                        .padding(12.dp)
//                        .size(40.dp)
//                        .background(
//                            color = Color.White,
//                            shape = CircleShape
//                        )
//                        .clickable { }
//                        .shadow(2.dp, CircleShape),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.FavoriteBorder,
//                        contentDescription = "Add to favorites",
//                        tint = Color.Gray,
//                        modifier = Modifier.size(20.dp)
//                    )
//                }
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

@Composable
fun FilterChips(modifier: Modifier = Modifier, viewModel: ProductsViewModel){

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        viewModel.options.forEach{ option ->
            val isSelected = viewModel.selectedOption.value == option

            OutlinedButton(
                onClick = {
                    viewModel.setSelectedOption(option)
                    viewModel.geFilteredProduct(option)
                          },
                shape = RoundedCornerShape(50),
                border = if (isSelected) BorderStroke(2.dp, Color.Black) else BorderStroke(0.dp, Color.Transparent),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                ),
            ){
                Text(
                    text = option,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,

                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductPreview(){
    ProductsScreen(type= "vans")
}

