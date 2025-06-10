package com.example.m_commerce.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.ResponseState
import com.example.m_commerce.domain.usecases.GetProductsByTypeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProductsViewModel @Inject constructor(private val productsUseCase: GetProductsByTypeUseCase): ViewModel() {
    private val _mutableProductsList : MutableStateFlow<ResponseState> = MutableStateFlow(
        ResponseState.Loading)
    val productsList: StateFlow<ResponseState> = _mutableProductsList.asStateFlow()


    fun getProductsByType(type: String) {
        viewModelScope.launch {
            _mutableProductsList.value = ResponseState.Loading

            val result = productsUseCase.invoke(type)
            result
                .catch {
                    _mutableProductsList.value = ResponseState.Failure(it)
                }
                .collect {
                    _mutableProductsList.value = ResponseState.Success(it)
                }
        }
    }

}


class ProductsViewModelFactory(private val productsUseCase: GetProductsByTypeUseCase): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProductsViewModel(productsUseCase) as T
    }
}