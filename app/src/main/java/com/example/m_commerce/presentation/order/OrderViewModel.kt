package com.example.m_commerce.presentation.order

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.ResponseState
import com.example.m_commerce.domain.repository.IOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OrderViewModel @Inject constructor(private val orderRepository: IOrderRepository) : ViewModel() {

    private val _mutableOrdersList: MutableStateFlow<ResponseState> =
        MutableStateFlow(ResponseState.Loading)
    val ordersList: StateFlow<ResponseState> = _mutableOrdersList.asStateFlow()

    fun getOrdersByCustomerId(customerId: String){
        Log.i("TAG", "getOrdersByCustomerId: ")
        viewModelScope.launch {
           val result = orderRepository.getOrdersByCustomerId(customerId)

            result.catch {
                _mutableOrdersList.value = ResponseState.Failure(it)
                Log.i("TAG", "Failure: $it")
            }.collect{
                _mutableOrdersList.value = ResponseState.Success(it)
                Log.i("TAG", "getOrdersByCustomerId: $it")
            }
        }
    }
}