package com.example.m_commerce.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.ResponseState
import com.example.m_commerce.domain.repository.IProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val productsRepository: IProductsRepository) : ViewModel() {

    private val _mutableBrandsList: MutableStateFlow<ResponseState> =
        MutableStateFlow(ResponseState.Loading)
    val brandsList: StateFlow<ResponseState> = _mutableBrandsList.asStateFlow()

    fun getBrands() {
        viewModelScope.launch {
            val result = productsRepository.getBrands()
            result
                .catch {
                    _mutableBrandsList.value = ResponseState.Failure(it)
                }
                .collect {
                    _mutableBrandsList.value = ResponseState.Success(it)
                }
        }
    }

}
