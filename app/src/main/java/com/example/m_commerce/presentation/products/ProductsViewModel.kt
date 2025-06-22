package com.example.m_commerce.presentation.products

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.presentation.utils.ResponseState
import com.example.m_commerce.domain.entities.Product
import com.example.m_commerce.domain.usecases.GetCurrencyPrefUseCase
import com.example.m_commerce.domain.usecases.GetProductsByTypeUseCase
import com.example.m_commerce.presentation.utils.Functions.filterProductsBySubType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productsUseCase: GetProductsByTypeUseCase,
    private val currencyPref: GetCurrencyPrefUseCase,

): ViewModel() {
    private val _mutableProductsList : MutableStateFlow<ResponseState> = MutableStateFlow(
        ResponseState.Loading)
    val productsList: StateFlow<ResponseState> = _mutableProductsList.asStateFlow()

    private var products : MutableList<Product>? = null

    private val _currencyPref = MutableStateFlow(Pair(true, 0f))
    val currencyPrefFlow: StateFlow<Pair<Boolean, Float>> = _currencyPref

    private val _selectedOption = mutableStateOf("ALL")
    val selectedOption: State<String> = _selectedOption


    val options = listOf("ALL", "SHOES", "ACCESSORIES")

    fun setSelectedOption(option: String) {
        _selectedOption.value = option
    }

    fun getProductsByType(type: String) {
        viewModelScope.launch {
            _mutableProductsList.value = ResponseState.Loading

            val result = productsUseCase(type)

            result
                .catch {
                    _mutableProductsList.value = ResponseState.Failure(it)
                }
                .collect {
                    _mutableProductsList.value = ResponseState.Success(it)
                    if(it.isNotEmpty()) products = ResponseState.Success(it).data as MutableList<Product>
                }
        }
    }

    fun geFilteredProduct(subType: String){

        if(subType == "ALL"){
            _mutableProductsList.value = products?.let { ResponseState.Success(it) }
                ?: ResponseState.Success(emptyList<String>())
            return
        }
        _mutableProductsList.value = (filterProductsBySubType(products, subType)?.let {
            ResponseState.Success(
                it
            )
        } ?: ResponseState.Success(emptyList<String>()))
    }


    fun getCurrencyPref() {
        viewModelScope.launch {
            _currencyPref.value  = currencyPref.invoke()
        }
    }

}
