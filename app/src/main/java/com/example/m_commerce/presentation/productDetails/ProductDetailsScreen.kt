package com.example.m_commerce.presentation.productDetails

import android.util.Log
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
import androidx.compose.ui.tooling.preview.Preview


val TAG = "ProductDetailsScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    productId: String,
    onBack: () -> Unit,
    viewModel: ProductDetailsViewModel = hiltViewModel()
) {

    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedVariantId by remember { mutableStateOf<String?>(null) }
    var selectedColor by remember { mutableStateOf<String?>(null) }
    var selectedSize by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    val productState by viewModel.productState.collectAsState()


    when (productState) {
        is ResponseState.Loading -> CircularProgressIndicator()
        is ResponseState.Failure -> Text("Failed to load product")
        is ResponseState.Success -> {
            val product = (productState as ResponseState.Success).data as Product
            Log.d(TAG, "Product: ${product.id}")

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
                                viewModel.addToCart(variantId, quantity)
                                Log.d(TAG, "Added to cart with variant ID: $variantId, quantity: $quantity")
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
                            onClick = {
                                Log.d(TAG, "Selected Variant ID: $selectedVariantId")
                                if (selectedVariantId != null) {
                                    showBottomSheet = true
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


//@Composable
//private fun VariantOptionsSection(
//    variants: List<ProductVariant>,
//    modifier: Modifier = Modifier
//) {
//    val colorOptions = variants
//        .flatMap { it.selectedOptions }
//        .filter { it.name.equals("Color", ignoreCase = true) }
//        .map { it.value }
//        .distinct()
//
//    val sizeOptions = variants
//        .flatMap { it.selectedOptions }
//        .filter { it.name.equals("Size", ignoreCase = true) }
//        .map { it.value }
//        .distinct()
//
//    Column(modifier = modifier) {
//        if (colorOptions.isNotEmpty()) {
//            Text(
//                text = "Colors",
//                style = MaterialTheme.typography.titleMedium.copy(
//                    fontWeight = FontWeight.Bold
//                )
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            LazyRow {
//                items(colorOptions) { color ->
//                    ElevatedFilterChip(
//                        selected = false,
//                        onClick = {},
//                        label = { Text(color) },
//                        modifier = Modifier.padding(end = 8.dp)
//                    )
//                }
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//        }
//
//        if (sizeOptions.isNotEmpty()) {
//            Text(
//                text = "Sizes",
//                style = MaterialTheme.typography.titleMedium.copy(
//                    fontWeight = FontWeight.Bold
//                )
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            LazyRow {
//                items(sizeOptions) { size ->
//                    ElevatedFilterChip(
//                        selected = false,
//                        onClick = {},
//                        label = { Text(size) },
//                        modifier = Modifier.padding(end = 8.dp)
//                    )
//                }
//            }
//        }
//    }
//}

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


