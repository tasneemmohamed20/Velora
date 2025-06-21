package com.example.m_commerce.presentation.productDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.domain.usecases.GetProductByIdUseCase
import com.example.m_commerce.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase
) : ViewModel() {

    private val _productState = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val productState: StateFlow<ResponseState> = _productState

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _productState.value = ResponseState.Loading
            try {
                val product = getProductByIdUseCase(productId)
                _productState.value = ResponseState.Success(product)
            } catch (e: Exception) {
                _productState.value = ResponseState.Failure(e)
            }
        }
    }
}