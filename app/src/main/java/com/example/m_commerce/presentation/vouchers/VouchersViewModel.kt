package com.example.m_commerce.presentation.vouchers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.domain.entities.DiscountCodes
import com.example.m_commerce.domain.usecases.DiscountCodesUsecse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VouchersViewModel @Inject constructor(
    private val discountCodesUseCase: DiscountCodesUsecse
) : ViewModel() {

    private val _discountCodes = MutableStateFlow<List<DiscountCodes>>(emptyList())
    val discountCodes: StateFlow<List<DiscountCodes>> = _discountCodes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchDiscountCodes()
    }

    private fun fetchDiscountCodes() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                discountCodesUseCase().collect { codes ->
                    _discountCodes.value = codes
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun retry() {
        fetchDiscountCodes()
    }
}
