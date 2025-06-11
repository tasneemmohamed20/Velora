package com.example.m_commerce.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.ResponseState
import com.example.m_commerce.domain.usecases.GetAllProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getAllProductsUseCase: GetAllProductsUseCase
) : ViewModel() {

    private val _productsList = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val productsList: StateFlow<ResponseState> = _productsList

    fun searchProductsByName(
        query: String,
        minPrice: Double? = null,
        maxPrice: Double? = null
    ) {
        viewModelScope.launch {
            _productsList.value = ResponseState.Loading
            getAllProductsUseCase().collect { products ->
                val filtered = products.filter { product ->
                    val price = product.price.minVariantPrice.amount.toDoubleOrNull()
                    val matchesName = if (query.isBlank()) true else product.title.startsWith(query, ignoreCase = true)
                    val matchesMin = minPrice == null || (price != null && price >= minPrice)
                    val matchesMax = maxPrice == null || (price != null && price <= maxPrice)
                    matchesName && matchesMin && matchesMax
                }
                _productsList.value = ResponseState.Success(filtered)
            }
        }
    }


}