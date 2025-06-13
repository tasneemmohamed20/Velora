package com.example.m_commerce.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.m_commerce.domain.entities.Product
import com.example.m_commerce.presentation.products.ProductCard
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import com.example.m_commerce.ResponseState
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.graphics.Color


@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val productsState by viewModel.productsList.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val minAllowedPrice by viewModel.minAllowedPrice.collectAsStateWithLifecycle()
    val maxAllowedPrice by viewModel.maxAllowedPrice.collectAsStateWithLifecycle()
    val currentMaxPrice by viewModel.currentMaxPrice.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = query,
            onValueChange = { viewModel.onQueryChange(it) },
            placeholder = { Text("Search by Product Name") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF0F0F0),
                focusedContainerColor = Color(0xFFF0F0F0),
                disabledContainerColor = Color(0xFFF0F0F0),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Gray
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Slider(
                value = currentMaxPrice.toFloat().coerceIn(minAllowedPrice.toFloat(), maxAllowedPrice.toFloat()),
                onValueChange = { viewModel.onMaxPriceChange(it.toDouble()) },
                valueRange = minAllowedPrice.toFloat()..maxAllowedPrice.toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.Blue,
                    activeTrackColor = Color.Blue,
                    inactiveTrackColor = Color.LightGray
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "%.2f $currency".format(viewModel.convertPrice(currentMaxPrice, currency)),
                style = MaterialTheme.typography.body2
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (productsState) {
                is ResponseState.Loading -> CircularProgressIndicator()
                is ResponseState.Success -> {
                    val products = (productsState as ResponseState.Success).data as List<Product>
                    if (products.isEmpty()) {
                        Text("No products found")
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(products) { product ->
                                ProductCard(product)
                            }
                        }
                    }
                }
                is ResponseState.Failure -> Text("Error loading products")
            }
        }
    }
}