package com.example.m_commerce.presentation.productDetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Optional
import com.example.m_commerce.domain.usecases.GetProductByIdUseCase
import com.example.m_commerce.ResponseState
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.DraftOrder
import com.example.m_commerce.domain.entities.Item
import com.example.m_commerce.domain.entities.LineItem
import com.example.m_commerce.domain.entities.note
import com.example.m_commerce.domain.usecases.DraftOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.toMutableList
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val draftOrderUseCase: DraftOrderUseCase
) : ViewModel() {

    private val _productState = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val productState: StateFlow<ResponseState> = _productState

    private val _favoriteState = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val favoriteState: StateFlow<ResponseState> = _favoriteState

    private val _cartState = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val cartState: StateFlow<ResponseState> = _cartState

    private val cartScope = viewModelScope

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _productState.value = ResponseState.Loading
            try {
                val product = getProductByIdUseCase(productId)
                _productState.value = ResponseState.Success(product)
                Log.d("ProductDetailsViewModel", "Product loaded: $product")
            } catch (e: Exception) {
                _productState.value = ResponseState.Failure(e)
            }
        }
    }

    private suspend fun getDraftOrder(customerId: String, noteType: note): Pair<Boolean, List<LineItem>> {
        var hasExistingOrder = false
        var currentLineItems = emptyList<LineItem>()

        try {
            withContext(Dispatchers.IO) {
                draftOrderUseCase(customerId)?.collect { draftOrders ->
                    if (draftOrders.note2 == noteType.name) {
                        _cartState.value = ResponseState.Success(draftOrders)
                        if (noteType.name == note.cart.name){
                            sharedPreferencesHelper.saveCartDraftOrderId(draftOrders.id ?: "")
                        } else if (noteType.name == note.fav.name) {
                            sharedPreferencesHelper.saveFavoriteDraftOrderId(draftOrders.id ?: "")
                        }
                        hasExistingOrder = true
                        currentLineItems = draftOrders.lineItems?.nodes?.filterNotNull()?.map { node ->
                            LineItem(
                                variantId = node.variantId ?: "",
                                quantity = node.quantity ?: 1
                            )
                        } ?: emptyList()

                        Log.d("ProductDetailsViewModel", "Existing cart found: $currentLineItems")
                    }
                }
            }
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> throw e
                else -> Log.e("ProductDetailsViewModel", "Error getting draft orders", e)
            }
        }
        return Pair(hasExistingOrder, currentLineItems)
    }

    fun addToCart(variantId: String, quantity: Int,noteType: note) {
        cartScope.launch {
            try {
                _cartState.value = ResponseState.Loading

                val customerEmail = sharedPreferencesHelper.getCustomerEmail()
                    ?: throw IllegalStateException("Customer email not found")
                val draftOrderId = sharedPreferencesHelper.getCartDraftOrderId()

                try {
                    val (hasExistingOrder, currentLineItems) = getDraftOrder(customerEmail.toString(), noteType)
                    Log.d("ProductDetailsViewModel", "Draft order check complete: hasExistingOrder=$hasExistingOrder, currentLineItems=$currentLineItems")
                    if (hasExistingOrder) {
                        updateExistingCart(variantId, currentLineItems, quantity, noteType)
                        Log.d("ProductDetailsViewModel", "Existing cart updated")
                    } else {
                        createNewCart(variantId, customerEmail, quantity,noteType)
                        Log.d("ProductDetailsViewModel", "New cart created")
                    }
                } catch (e: Exception) {
                    // If getDraftOrder fails, create a new cart
                    createNewCart(variantId, customerEmail, quantity,noteType)
                    Log.d("ProductDetailsViewModel", "Created new cart after getDraftOrder failed")
                }

            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    else -> {
                        Log.e("ProductDetailsViewModel", "Error adding to cart", e)
                        _cartState.value = ResponseState.Failure(e)
                    }
                }
            }
        }
    }

    private suspend fun createNewCart(variantId: String, customerEmail: String, quantity: Int,noteType: note) {
        val lineItems = listOf(
            LineItem(
                id = variantId,
                quantity = quantity,
                requiresShipping = true,
                taxable = true
            )
        )

        val draftOrder = withContext(Dispatchers.IO) {
            draftOrderUseCase(
                lineItems = lineItems,
                variantId = variantId,
                note = Optional.present(noteType.name),
                email = customerEmail,
                quantity = quantity
            )
        }

        if (draftOrder.id != null) {
            _cartState.value = ResponseState.Success(draftOrder)
            sharedPreferencesHelper.saveCartDraftOrderId(draftOrder.id)
        } else {
            throw IllegalStateException("Failed to create cart - no order ID returned")
        }
    }

    private suspend fun updateExistingCart(variantId: String, existingLineItems: List<LineItem>, quantity: Int , noteType: note) {
        try {
            // Convert LineItem list to Item list while preserving quantities
            val existingItems = existingLineItems.mapNotNull { lineItem ->
                lineItem.variantId?.takeIf { id ->
                    id.startsWith("gid://shopify/ProductVariant/")
                }?.let {
                    Item(variantID = it, quantity = lineItem.quantity ?: 0)
                }
            }

            val formattedVariantId = if (!variantId.startsWith("gid://shopify/ProductVariant/")) {
                "gid://shopify/ProductVariant/$variantId"
            } else {
                variantId
            }

            val updatedItems = existingItems.toMutableList()
            Log.d("ProductDetailsViewModel", "Existing items before update: $updatedItems")

            val existingItemIndex = updatedItems.indexOfFirst { it.variantID == formattedVariantId }

            if (existingItemIndex != -1) {
                val currentQuantity = updatedItems[existingItemIndex].quantity ?: 0
                val newQuantity = currentQuantity + quantity
                if (newQuantity > 0) {
                    updatedItems[existingItemIndex] = Item(
                        variantID = formattedVariantId,
                        quantity = newQuantity
                    )
                } else {
                    // Remove if total quantity becomes 0 or negative
                    updatedItems.removeAt(existingItemIndex)
                }
            } else if (quantity > 0) {
                // Add new item if it doesn't exist and quantity > 0
                updatedItems.add(Item(
                    variantID = formattedVariantId,
                    quantity = quantity
                ))
            }

            Log.d("ProductDetailsViewModel", "Updated items after modification: $updatedItems")
            var draftOrderId = ""
            if (noteType.name == note.cart.name) {
                draftOrderId = sharedPreferencesHelper.getCartDraftOrderId().toString()
            } else if (noteType.name == note.fav.name) {
                draftOrderId = sharedPreferencesHelper.getFavoriteDraftOrderId().toString()
            }


            val updatedDraftOrder = withContext(Dispatchers.IO) {
                draftOrderUseCase(
                    id = draftOrderId,
                    lineItems = updatedItems
                )
            }

            _cartState.value = ResponseState.Success(updatedDraftOrder)
            Log.d("ProductDetailsViewModel", "Cart updated successfully with ${updatedItems.size} items")
            Log.d("ProductDetailsViewModel", "Final cart items: ${updatedItems.map { "${it.variantID}: ${it.quantity}" }}")

        } catch (e: Exception) {
            _cartState.value = ResponseState.Failure(e)
            Log.e("ProductDetailsViewModel", "Error updating cart", e)
        }
    }

//    fun addToFavorites(variantId: String) {
//        viewModelScope.launch {
//            try {
//                _favoriteState.value = ResponseState.Loading
//                val customerEmail = sharedPreferencesHelper.getCustomerEmail()
//                    ?: throw IllegalStateException("Customer email not found")
//
//                val (hasExistingOrder, currentLineItems) = getDraftOrder(customerEmail, note.fav)
//                if (hasExistingOrder) {
//                    updateExistingFavorites(variantId, currentLineItems, 1)
//                } else {
//                    createNewFavorites(variantId, customerEmail)
//                }
//            } catch (e: Exception) {
//                _favoriteState.value = ResponseState.Failure(e)
//            }
//        }
//    }
//
//    private suspend fun createNewFavorites(variantId: String, customerEmail: String) {
//        val lineItems = listOf(
//            LineItem(
//                id = variantId,
//                requiresShipping = true,
//                taxable = true
//            )
//        )
//
//        val draftOrder = withContext(Dispatchers.IO) {
//            draftOrderUseCase(
//                lineItems = lineItems,
//                variantId = variantId,
//                note = Optional.present(note.fav.name),
//                email = customerEmail,
//                quantity = 1
//            )
//        }
//        Log.d("ProductDetailsViewModel", "DraftOrder after create: $draftOrder")
//        Log.d("ProductDetailsViewModel", "LineItems: ${draftOrder.lineItems?.nodes}")
//
//
//        if (draftOrder.id != null) {
//            _favoriteState.value = ResponseState.Success(draftOrder)
//            sharedPreferencesHelper.saveFavoriteDraftOrderId(draftOrder.id)
//        } else {
//            throw IllegalStateException("Failed to create favorites (no order ID returned)")
//        }
//    }
//
//    private suspend fun updateExistingFavorites(variantId: String, existingLineItems: List<LineItem>, quantity: Int) {
//        try {
//            val existingItems = existingLineItems.mapNotNull { lineItem ->
//                lineItem.variantId?.let {
//                    Item(variantID = it, quantity = 0)
//                }
//            }
//
//            val formattedVariantId = if (!variantId.startsWith("gid://shopify/ProductVariant/")) {
//                "gid://shopify/ProductVariant/$variantId"
//            } else {
//                variantId
//            }
//
//            val updatedItems = existingItems.toMutableList()
//            val exists = updatedItems.any { it.variantID == formattedVariantId }
//
//            if (!exists) {
//                updatedItems.add(Item(variantID = formattedVariantId, quantity = 0))
//            }
//            // No quantity logic
//
//            val draftOrderId = sharedPreferencesHelper.getFavoriteDraftOrderId()
//                ?: throw IllegalStateException("Favorite draft order ID not found")
//
//            val updatedDraftOrder = withContext(Dispatchers.IO) {
//                draftOrderUseCase(
//                    id = draftOrderId,
//                    lineItems = updatedItems
//                )
//            }
//
//            _favoriteState.value = ResponseState.Success(updatedDraftOrder)
//        } catch (e: Exception) {
//            _favoriteState.value = ResponseState.Failure(e)
//        }
//    }
//


}