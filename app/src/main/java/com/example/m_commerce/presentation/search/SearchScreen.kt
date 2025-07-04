package com.example.m_commerce.presentation.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Down
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Up
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.with
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
import com.example.m_commerce.presentation.utils.ResponseState
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.Color
import com.example.m_commerce.presentation.products.NoProductsFound
import com.example.m_commerce.presentation.utils.theme.Primary


@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onProductClick: (String) -> Unit
) {
    val productsState by viewModel.productsList.collectAsStateWithLifecycle()
    val currency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val minAllowedPrice by viewModel.minAllowedPrice.collectAsStateWithLifecycle()
    val maxAllowedPrice by viewModel.maxAllowedPrice.collectAsStateWithLifecycle()
    val currentMaxPrice by viewModel.currentMaxPrice.collectAsStateWithLifecycle()
    val currencyPrefState by viewModel.currencyPrefFlow.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
        viewModel.getCurrencyPref()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = query,
            onValueChange = { viewModel.onQueryChange(it) },
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
            label = {
                Text("Search by Product Name",
                    color = Color.Gray.copy(alpha = 0.5f),
                    style = androidx.compose.material3.MaterialTheme.typography.titleSmall
                    )
                },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Black,
                    modifier = Modifier.size(30.dp)
                )
            },
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
            AnimatedContent(
                targetState = productsState,
                transitionSpec =  {
                    slideIntoContainer(
                        animationSpec = tween(300, easing = EaseIn),
                        towards = Up
                    ).with(
                       slideOutOfContainer(
                           animationSpec = tween(300, easing = EaseOut),
                           towards = Down
                       )
                    )
                }
            ) { productsState ->
                when (productsState) {
                    is ResponseState.Loading -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularWavyProgressIndicator(color = Primary.copy(alpha = 0.7f))
                    }
                    is ResponseState.Success -> {
                        val products = productsState.data as List<Product>
                        if (products.isEmpty()) {
                            NoProductsFound(text = "No products found, check for spelling, or can try with other words")
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(products) { product ->
                                    ProductCard(product, onProductClick = onProductClick, currencyPref = currencyPrefState)
                                }
                            }
                        }
                    }
                    is ResponseState.Failure -> Text("Error loading products")
                }
            }

        }
    }
}