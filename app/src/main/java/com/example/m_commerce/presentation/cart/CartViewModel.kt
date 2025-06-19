package com.example.m_commerce.presentation.cart

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.ResponseState
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.DraftOrder
import com.example.m_commerce.domain.entities.Item
import com.example.m_commerce.domain.entities.note
import com.example.m_commerce.domain.usecases.DraftOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class CartViewModel @Inject constructor(
    private val draftOrderUseCase: DraftOrderUseCase,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : ViewModel() {

    private val _cartState = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val cartState: StateFlow<ResponseState> = _cartState
    var currentOrder : DraftOrder? = null
    val draftOrderID = sharedPreferencesHelper.getCartDraftOrderId()
    init {
        loadCartItems()

    }

    fun loadCartItems() {
        viewModelScope.launch {
            try {
                val customerId = sharedPreferencesHelper.getCustomerId()
                    ?: throw Exception("Customer ID not found")
                getDraftOrder(customerId, note.cart)
                Log.d("CartViewModel", note.cart.name)
            } catch (e: Exception) {
                _cartState.value = ResponseState.Failure(e)
                Log.e("CartViewModel", "Error loading cart items", e)
            }
        }
    }

    private suspend fun getDraftOrder(customerId: String, noteType: note) {
        try {
            draftOrderUseCase(customerId)?.collect { draftOrders ->
                if (draftOrders.note2 == noteType.name) {
                    currentOrder = draftOrders
                    _cartState.value = ResponseState.Success(draftOrders)
                    Log.d("CartViewModel", "Found cart: ${draftOrders.note2}")
                    Log.d("CartViewModel", "Found cart: ${draftOrders}")
                }
            }
        } catch (e: Exception) {
            Log.e("CartViewModel", "Error getting draft orders", e)
            _cartState.value = ResponseState.Failure(e)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun updateQuantity(variantId: String, newQuantity: Int) {
       viewModelScope.launch {
            try {
                _cartState.value = ResponseState.Loading

                    Log.d("CartViewModel", "Current Order: $currentOrder")
                val updatedItems = currentOrder?.lineItems?.nodes?.mapNotNull { node ->
                    if (node.id == variantId) {
                        Item(
                            variantID = node.variantId ?: return@mapNotNull null,
                            quantity = newQuantity
                        )
                    } else {
                        Item(
                            variantID = node.variantId ?: return@mapNotNull null,
                            quantity = node.quantity ?: 1
                        )
                    }
                } ?: emptyList()
                Log.d("CartViewModel", "Updated Items: $updatedItems")
                val updatedOrder = draftOrderUseCase(
                    id = draftOrderID.toString(),
                    lineItems = updatedItems
                )
                loadCartItems()
                _cartState.value = ResponseState.Success(updatedOrder)
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    else -> _cartState.value = ResponseState.Failure(e)
                }
            }
        }
    }

    fun removeItem(variantId: String) {
        viewModelScope.launch {
            try {
                _cartState.value = ResponseState.Loading
                Log.d("CartViewModel", "Current Order: ${currentOrder?.lineItems}")
                val nodeVariatId : String? = currentOrder?.lineItems?.nodes?.find { it.variantId == variantId }?.variantId
                val updatedItems = currentOrder?.lineItems?.nodes
                    ?.filterNot {
                        it.id == variantId
                    }
                    ?.mapNotNull { node ->
                        Item(
                            variantID = node.variantId ?: return@mapNotNull null,
                            quantity = node.quantity ?: 1
                        )
                    } ?: emptyList()
                Log.d("CartViewModel", "Updated Order: $updatedItems")
                Log.d("compare", "viriantID: $variantId, node.variantId: ${nodeVariatId.toString()}")
                val updatedOrder = draftOrderUseCase(
                    id = draftOrderID.toString(),
                    lineItems = updatedItems
                )
                loadCartItems()
                _cartState.value = ResponseState.Success(updatedOrder)
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    else -> _cartState.value = ResponseState.Failure(e)
                }
            }
        }
    }
}