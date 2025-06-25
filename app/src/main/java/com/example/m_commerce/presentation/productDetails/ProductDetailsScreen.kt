package com.example.m_commerce.presentation.productDetails

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import com.example.m_commerce.presentation.utils.ResponseState
import com.example.m_commerce.domain.entities.ProductVariant
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import com.example.m_commerce.domain.entities.note
import com.example.m_commerce.presentation.favorite.FavoriteHeartIcon
import com.example.m_commerce.presentation.favorite.FavoriteViewModel
import com.example.m_commerce.presentation.utils.theme.Primary
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.note
import com.example.m_commerce.presentation.favorite.FavoriteHeartIcon
import com.example.m_commerce.presentation.favorite.FavoriteViewModel
import com.example.m_commerce.presentation.utils.components.PendingAction
import com.example.m_commerce.presentation.utils.routes.ScreensRoute
import kotlinx.coroutines.launch


val TAG = "ProductDetailsScreen"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProductDetailsScreen(
    productId: String,
    onBack: () -> Unit,
    viewModel: ProductDetailsViewModel = hiltViewModel(),
    favoriteViewModel: FavoriteViewModel = hiltViewModel(),
    navController: NavController,
) {
    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedVariantId by remember { mutableStateOf<String?>(null) }
    var selectedColor by remember { mutableStateOf<String?>(null) }
    var selectedSize by remember { mutableStateOf<String?>(null) }

    val favoriteVariantIds by favoriteViewModel.favoriteVariantIds.collectAsState()

    val context = LocalContext.current
    val sharedPrefsHelper = remember { SharedPreferencesHelper(context) }
    val coroutineScope = rememberCoroutineScope()
    var showLoginDialog by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf(PendingAction.NONE) }
    var showConfirmRemoveDialog by remember { mutableStateOf(false) }


    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
        favoriteViewModel.loadFavorites()
    }

    val productState by viewModel.productState.collectAsState()

    when (productState) {
        is ResponseState.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularWavyProgressIndicator(color = Primary.copy(alpha = 0.7f))
        }

        is ResponseState.Failure -> Text("Failed to load product")
        is ResponseState.Success -> {
            val product = (productState as ResponseState.Success).data as Product
            val firstVariantId = product.variants.firstOrNull()?.id ?: ""
            val isFavorited = favoriteVariantIds.contains(firstVariantId)

            Log.d(
                "ProductDetailsScreen",
                "Product Price: ${product.price.minVariantPrice.amount} ${product.price.minVariantPrice.currencyCode}"
            )
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = bottomSheetState
                ) {
                    ProductBottomSheet(
                        product = product,
                        selectedColor = selectedColor,
                        selectedSize = selectedSize,
                        onConfirm = { quantity ->
                            selectedVariantId?.let { variantId ->
                                viewModel.addToCart(variantId, quantity, note.cart)
                                Log.d(
                                    TAG,
                                    "Added to cart with variant ID: $variantId, quantity: $quantity"
                                )
                            }
                            showBottomSheet = false
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Box {
                    ImageCarousel(
                        urls = product.images,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
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
                    }


                }

                Spacer(modifier = Modifier.height(24.dp))

                VariantOptionsSection(
                    variants = product.variants,
                    modifier = Modifier.fillMaxWidth(),
                    onVariantSelected = { variantId ->
                        selectedVariantId = variantId
                    },
                    selectedColor = selectedColor,
                    selectedSize = selectedSize,
                    onColorSelected = { color -> selectedColor = color },
                    onSizeSelected = { size -> selectedSize = size }
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
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter,

                ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
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
                        FavoriteHeartIcon(
                            isFavorited = isFavorited,
                            onToggle = {
                                if (sharedPrefsHelper.isGuestMode()) {
                                    pendingAction = PendingAction.ADD_TO_FAVORITES
                                    showLoginDialog = true
                                } else {
                                    if (isFavorited) {
                                        showConfirmRemoveDialog = true
                                    } else {
                                        favoriteViewModel.toggleProductFavorite(product, firstVariantId)
                                        Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show()                                    }
                                }
                            },
                            modifier = Modifier
                                .size(70.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    CircleShape
                                ),
                            size = 42.dp
                        )


                        Button(
                            onClick = {
                                Log.d(TAG, "Selected Variant ID: $selectedVariantId")
                                if (sharedPrefsHelper.isGuestMode()) {
                                    pendingAction = PendingAction.ADD_TO_CART
                                    showLoginDialog = true
                                } else {
                                    if (selectedVariantId != null) {
                                        showBottomSheet = true
                                    }
                                }
                            },
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
                                ),
                            )
                        }
                    }
                    if (showLoginDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                showLoginDialog = false
                                pendingAction = PendingAction.NONE
                            },
                            title = {
                                Text("Sign in Required")
                            },
                            text = {
                                Text("Please sign in to ${if (pendingAction == PendingAction.ADD_TO_CART) "add products to your cart" else "add products to your favorites"}.")
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showLoginDialog = false
                                        navController.navigate(ScreensRoute.SignUp)
                                    }
                                ) {
                                    Text("Sign In")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        showLoginDialog = false
                                        pendingAction = PendingAction.NONE
                                    }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }


                    if (showConfirmRemoveDialog) {
                        AlertDialog(
                            onDismissRequest = { showConfirmRemoveDialog = false },
                            title = { Text("Remove from Favorites?") },
                            text = { Text("Are you sure you want to remove this product from your favorites?") },
                            confirmButton = {
                                TextButton(onClick = {
                                    favoriteViewModel.toggleProductFavorite(product, firstVariantId)
                                    showConfirmRemoveDialog = false
                                }) {
                                    Text("Remove")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showConfirmRemoveDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
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
    modifier: Modifier = Modifier,
    onVariantSelected: (String) -> Unit = {},
    selectedColor: String? = null,
    selectedSize: String? = null,
    onColorSelected: (String?) -> Unit,
    onSizeSelected: (String?) -> Unit
){
    var selectedVariantId by remember { mutableStateOf<String?>(null) }

    val colorOptions = variants
        .flatMap { variant ->
            variant.selectedOptions?.filterNotNull() ?: emptyList()
        }
        .filter { option ->
            option.name.equals("Color", ignoreCase = true)
        }
        .distinctBy { option ->
            option.value
        }

    val filteredSizeOptions = variants
        .filter { variant ->
            selectedColor == null || variant.selectedOptions?.any { option ->
                option?.let {
                    it.name.equals("Color", ignoreCase = true) &&
                            it.value == selectedColor
                } ?: false
            } ?: false
        }
        .flatMap { variant ->
            variant.selectedOptions?.filterNotNull() ?: emptyList()
        }
        .filter { option ->
            option.name.equals("Size", ignoreCase = true)
        }
        .map { option ->
            option.value
        }
        .distinct()

    LaunchedEffect(selectedColor, selectedSize) {
        if (selectedColor != null && selectedSize != null) {
            val matchingVariant = variants.find { variant ->
                variant.selectedOptions?.any { it?.name.equals("Color", ignoreCase = true) && it?.value == selectedColor } == true &&
                        variant.selectedOptions.any { it?.name.equals("Size", ignoreCase = true) && it?.value == selectedSize }
            }
            selectedVariantId = matchingVariant?.id
            selectedVariantId?.let { onVariantSelected(it) }
        }
    }

    Column(modifier = modifier) {
        if (colorOptions.isNotEmpty()) {
            Text(
                text = "Colors",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow {
                items(colorOptions) { color ->
                    ElevatedFilterChip(
                        selected = selectedColor == color.value,
                        onClick = {
                            onColorSelected(if (selectedColor == color.value) null else color.value)
                            onSizeSelected(null)
                        },
                        label = { Text(color.value) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (filteredSizeOptions.isNotEmpty()) {
            Text(
                text = "Sizes",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow {
                items(filteredSizeOptions) { size ->
                    ElevatedFilterChip(
                        selected = selectedSize == size,
                        onClick = { onSizeSelected(if (selectedSize == size) null else size) },
                        label = { Text(size) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductBottomSheet(
    product: Product,
    selectedColor: String?,
    selectedSize: String?,
    onConfirm: (Int) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(product.images.firstOrNull()),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )

            Column {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Color: ${selectedColor ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Size: ${selectedSize ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quantity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = { if (quantity > 1) quantity-- },
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            CircleShape
                        )
                ) {
                    Text("-", style = MaterialTheme.typography.titleMedium)
                }
                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(
                    onClick = { quantity++ },
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            CircleShape
                        )
                ) {
                    Text("+", style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onConfirm(quantity) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)

        ) {
            Text("CONFIRM")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

