package com.example.m_commerce.presentation.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.Product
import com.example.m_commerce.presentation.utils.theme.Primary

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FavoriteView(
    viewModel: FavoriteViewModel = hiltViewModel(),
    onProductClick: (String) -> Unit
) {
    val favoriteProducts by viewModel.favoriteProducts.collectAsState()
    val favoriteVariantIds by viewModel.favoriteVariantIds.collectAsState()
    val context = LocalContext.current
    val sharedPreferencesHelper = remember { SharedPreferencesHelper(context) }

    val isGuest = remember { sharedPreferencesHelper.getCustomerEmail() == null }
    val isLoading by viewModel.isLoading.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToRemove by remember { mutableStateOf<Pair<Product, String>?>(null) }

    LaunchedEffect(Unit) {
        if (!isGuest) viewModel.loadFavorites()
    }

    when {
        !isGuest && isLoading -> {
            CircularWavyProgressIndicator(
                stroke = Stroke(width = 4.0f, cap = StrokeCap.Round),
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                color = Primary.copy(alpha = 0.7f)
            )
        }

        isGuest -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Favorites",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Please login to view your favorites",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        favoriteProducts.isEmpty() -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Favorites",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No favorite products added yet!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        else -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favoriteProducts.size) { index ->
                    val product = favoriteProducts[index]
                    val variantId = product.variants.firstOrNull()?.id ?: ""
                    val isFavorited = favoriteVariantIds.contains(variantId)

                    FavoriteProductCard(
                        product = product,
                        isFavorited = isFavorited,
                        onProductClick = { onProductClick(product.id) },
                        onFavoriteToggle = {
                            productToRemove = product to variantId
                            showDeleteDialog = true
                        }
                    )
                }
            }

            if (showDeleteDialog && productToRemove != null) {
                val (product, variantId) = productToRemove!!
                AlertDialog(
                    onDismissRequest = {
                        showDeleteDialog = false
                        productToRemove = null
                    },
                    title = { Text("Remove from Favorites") },
                    text = { Text("Are you sure you want to remove this product from your favorites?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.toggleProductFavorite(product, variantId)
                                showDeleteDialog = false
                                productToRemove = null
                            }
                        ) {
                            Text("Remove")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                productToRemove = null
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}



@Composable
private fun FavoriteProductCard(
    product: Product,
    isFavorited: Boolean,
    onProductClick: (String) -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clickable { onProductClick(product.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = product.images.firstOrNull(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                FavoriteHeartIcon(
                    isFavorited = isFavorited,
                    onToggle = onFavoriteToggle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape),
                    size = 20.dp
                )
            }

            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = product.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun FavoriteHeartIcon(
    isFavorited: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    IconButton(
        onClick = onToggle,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isFavorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isFavorited) "Remove from favorites" else "Add to favorites",
            tint = if (isFavorited) Color.Red else Color.Gray,
            modifier = Modifier.size(size)
        )
    }
}
