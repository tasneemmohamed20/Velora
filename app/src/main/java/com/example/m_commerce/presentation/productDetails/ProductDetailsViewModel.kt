package com.example.m_commerce.presentation.productDetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.api.Optional
import com.example.m_commerce.domain.usecases.GetProductByIdUseCase
import com.example.m_commerce.ResponseState
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.note
import com.example.m_commerce.domain.usecases.DraftOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val draftOrderUseCase: DraftOrderUseCase
) : ViewModel() {

    private val _productState = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val productState: StateFlow<ResponseState> = _productState

    private val _cartState = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val cartState: StateFlow<ResponseState> = _cartState



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

    private suspend fun getDraftOrder(customerId: String, noteType: note): Boolean {
        var hasExistingOrder = false
        try {
            draftOrderUseCase(customerId)?.collect { draftOrders ->
                if (draftOrders.note2 == noteType.name) {
                    _cartState.value = ResponseState.Success(draftOrders)
                    hasExistingOrder = true
                    Log.d("ProductDetailsViewModel", "Found existing draft order with note: ${draftOrders.note2}")
                }
            }
        } catch (e: Exception) {
            Log.e("ProductDetailsViewModel", "Error getting draft orders", e)
            _cartState.value = ResponseState.Failure(e)
        }
        return hasExistingOrder
    }

    private suspend fun createCart(variantId: String, customerId: String) {
        val draftOrder = draftOrderUseCase(
            lineItems = emptyList(),
            variantId = variantId,
            note = Optional.present(note.cart.name),
            email = customerId,
            quantity = 1
        )
        _cartState.value = ResponseState.Success(draftOrder)
        Log.d("ProductDetailsViewModel", "Created new cart with variantId: $variantId")
    }

    fun addToCart(variantId: String) {
        viewModelScope.launch {
            _cartState.value = ResponseState.Loading
            try {
                val customerId = sharedPreferencesHelper.getCustomerId()
                    ?: throw Exception("Customer ID not found")

                val hasExistingOrder = getDraftOrder(customerId, note.cart)
                if (!hasExistingOrder) {
                    createCart(variantId, customerId)
                }
            } catch (e: Exception) {
                _cartState.value = ResponseState.Failure(e)
                Log.e("ProductDetailsViewModel", "Error adding to cart", e)
            }
        }
    }
}