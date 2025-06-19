package com.example.m_commerce.presentation.productDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.m_commerce.domain.entities.Product
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import com.example.m_commerce.ResponseState
import com.example.m_commerce.domain.entities.ProductVariant
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow


@Composable
fun ProductDetailsScreen(
    productId: String,
    onBack: () -> Unit,
    viewModel: ProductDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    val productState by viewModel.productState.collectAsState()


    when (productState) {
        is ResponseState.Loading -> CircularProgressIndicator()
        is ResponseState.Failure -> Text("Failed to load product")
        is ResponseState.Success -> {
            val product = (productState as ResponseState.Success).data as Product

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                ImageCarousel(
                    urls = product.images,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Price: ${product.price.minVariantPrice.amount} ${product.price.minVariantPrice.currencyCode}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                VariantOptionsSection(
                    variants = product.variants,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(product.description)

                Spacer(modifier = Modifier.height(100.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                modifier = Modifier.size(42.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Button(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 6.dp,
                                pressedElevation = 8.dp
                            )
                        ) {
                            Text(
                                text = "ADD TO CART",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageCarousel(urls: List<String>, modifier: Modifier = Modifier) {

    val pagerState = rememberPagerState { urls.size }

    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            Image(
                painter = rememberAsyncImagePainter(urls[page]),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Fit
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(urls.size) { index ->
                val selected = pagerState.currentPage == index
                Box(
                    Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (selected) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                )
            }
        }
    }
}


@Composable
private fun VariantOptionsSection(
    variants: List<ProductVariant>,
    modifier: Modifier = Modifier
) {
    val colorOptions = variants
        .flatMap { it.selectedOptions }
        .filter { it.name.equals("Color", ignoreCase = true) }
        .map { it.value }
        .distinct()

    val sizeOptions = variants
        .flatMap { it.selectedOptions }
        .filter { it.name.equals("Size", ignoreCase = true) }
        .map { it.value }
        .distinct()

    Column(modifier = modifier) {
        if (colorOptions.isNotEmpty()) {
            Text(
                text = "Colors",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow {
                items(colorOptions) { color ->
                    ElevatedFilterChip(
                        selected = false,
                        onClick = {},
                        label = { Text(color) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (sizeOptions.isNotEmpty()) {
            Text(
                text = "Sizes",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow {
                items(sizeOptions) { size ->
                    ElevatedFilterChip(
                        selected = false,
                        onClick = {},
                        label = { Text(size) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }
    }
}