package com.example.m_commerce.presentation.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.ResponseState
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.entities.note
import com.example.m_commerce.domain.usecases.DraftOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val draftOrderUseCase: DraftOrderUseCase,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : ViewModel() {

    private val _cartState = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val cartState: StateFlow<ResponseState> = _cartState

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

    fun updateQuantity(variantId: String, newQuantity: Int) {
        viewModelScope.launch {
            try {
                // Implement quantity update logic here
                loadCartItems() // Reload cart after update
            } catch (e: Exception) {
                _cartState.value = ResponseState.Failure(e)
            }
        }
    }

    fun removeItem(variantId: String) {
        viewModelScope.launch {
            try {
                // Implement remove item logic here
                loadCartItems() // Reload cart after removal
            } catch (e: Exception) {
                _cartState.value = ResponseState.Failure(e)
            }
        }
    }
}