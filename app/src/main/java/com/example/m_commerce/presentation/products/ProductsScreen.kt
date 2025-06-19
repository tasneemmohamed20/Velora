
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
import kotlin.math.log

@Composable
fun ProductsScreen(viewModel: ProductsViewModel = hiltViewModel(), type: String,onProductClick: (String) -> Unit){

    LaunchedEffect(Unit) {
        viewModel.getProductsByType(type)
        viewModel.getCurrencyPref()
    }
    val productsState by viewModel.productsList.collectAsStateWithLifecycle()
    val currencyPrefState by viewModel.currencyPrefFlow.collectAsStateWithLifecycle()

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
                    val productsData = productsState.data as List<Product>

                    ProductsList(productsData, currencyPrefState, Modifier.weight(2F),onProductClick = onProductClick)
                }
            }
        }


    }

}

@Composable
fun ProductsList(productsState: List<Product>, currencyPref: Pair<Boolean, Float>, modifier: Modifier = Modifier,onProductClick: (String) -> Unit){

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .background(color = WhiteSmoke)
            .padding(top = 16.dp, start = 8.dp, end = 8.dp)
    ) {
        items(productsState.size){
            ProductCard(productsState[it], currencyPref = currencyPref, onProductClick = onProductClick)
        }
    }
}

@Composable
fun ProductCard(
    productDetails: Product,
    modifier: Modifier = Modifier,
    currencyPref: Pair<Boolean, Float> = Pair(false, 1f),
    onProductClick: (String) -> Unit
) {
    val (brand, productName) = formatTitleAndBrand(productDetails.title)

    val egpPrice = productDetails.price.minVariantPrice.amount.toFloatOrNull() ?: 0f
    val prefersUSD = currencyPref.first
    val egpToUsdRate = currencyPref.second.takeIf { it != 0f } ?: 1f
    val finalPrice = if (prefersUSD) egpPrice / egpToUsdRate else egpPrice
    val currencySymbol = if (prefersUSD) "USD" else "EGP"

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val itemWidth = (screenWidth - 32.dp) / 2

    Log.i("TAG", "ProductCard: $productDetails")
    Card(
        modifier = modifier
            .width(itemWidth)
            .padding(bottom = 8.dp)
            .clickable {
                onProductClick(productDetails.id)
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(modifier = Modifier.height(150.dp)) {
                SubcomposeAsyncImage(
                    model = productDetails.images.firstOrNull(),
                    loading = {
                        CircularProgressIndicator()
                    },
                    error = {},
                    contentDescription = "Product image",
                    modifier = Modifier.size(140.dp),
                    contentScale = ContentScale.Inside
                )
            }

            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .height(80.dp)
            ) {
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
                        text = "%.2f $currencySymbol".format(finalPrice),
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
    ProductsScreen(type= "vans", onProductClick = {})
}

