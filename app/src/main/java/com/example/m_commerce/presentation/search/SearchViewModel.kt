package com.example.m_commerce.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.ResponseState
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.usecases.GetAllProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : ViewModel() {

    private val _productsList = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val productsList: StateFlow<ResponseState> = _productsList

    private val _selectedCurrency = MutableStateFlow(getCurrencyCode())
    val selectedCurrency: StateFlow<String> = _selectedCurrency

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _minAllowedPrice = MutableStateFlow(0.0)
    val minAllowedPrice: StateFlow<Double> = _minAllowedPrice

    private val _maxAllowedPrice = MutableStateFlow(1000.0)
    val maxAllowedPrice: StateFlow<Double> = _maxAllowedPrice

    private val _currentMinPrice = MutableStateFlow(0.0)

    private val _currentMaxPrice = MutableStateFlow(1000.0)
    val currentMaxPrice: StateFlow<Double> = _currentMaxPrice

    private val usdToEgpRate = 48.0

    init {
        fetchProductsAndInitPrices()
    }

    private fun fetchProductsAndInitPrices() {
        viewModelScope.launch {
            _productsList.value = ResponseState.Loading
            getAllProductsUseCase().collect { products ->
                val prices = products.mapNotNull { it.price.minVariantPrice.amount.toDoubleOrNull() }
                val minPrice = prices.minOrNull() ?: 0.0
                val maxPrice = prices.maxOrNull() ?: 1000.0
                _minAllowedPrice.value = minPrice
                _maxAllowedPrice.value = maxPrice
                _currentMinPrice.value = minPrice
                _currentMaxPrice.value = maxPrice
                _productsList.value = ResponseState.Success(products)
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        searchProducts()
    }

    fun onMaxPriceChange(newMax: Double) {
        _currentMaxPrice.value = newMax
        searchProducts()
    }

    private fun searchProducts() {
        viewModelScope.launch {
            _productsList.value = ResponseState.Loading
            getAllProductsUseCase().collect { products ->
                val filtered = products.filter { product ->
                    val price = product.price.minVariantPrice.amount.toDoubleOrNull()
                    val matchesName = if (_query.value.isBlank()) true else product.title.contains(_query.value, ignoreCase = true)
                    val matchesMin = price != null && price >= _currentMinPrice.value
                    val matchesMax = price != null && price <= _currentMaxPrice.value
                    matchesName && matchesMin && matchesMax
                }
                _productsList.value = ResponseState.Success(filtered)
            }
        }
    }

    private fun getCurrencyCode(): String {
        return if (sharedPreferencesHelper.getCurrencyPreference()) "USD" else "EGP"
    }

    fun convertPrice(amount: Double, currency: String): Double {
        return if (currency == "USD") amount / usdToEgpRate else amount
    }
}