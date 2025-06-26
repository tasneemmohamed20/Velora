package com.example.m_commerce.presentation.order.orders_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.presentation.utils.ResponseState
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.repository.IOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: IOrderRepository,
    private val sharedPreferencesHelper: SharedPreferencesHelper,
) : ViewModel() {

    private val _mutableOrdersList: MutableStateFlow<ResponseState> =
        MutableStateFlow(ResponseState.Loading)
    val ordersList: StateFlow<ResponseState> = _mutableOrdersList.asStateFlow()

    fun getOrdersByCustomer(){

        val customerId = sharedPreferencesHelper.getCustomerId()?.split("/")?.last() ?: ""
        Log.i("getOrdersByCustomer", "getOrdersByCustomer: $customerId")
        viewModelScope.launch {
           val result = orderRepository.getOrdersByCustomerId(customerId)
            Log.i("getOrdersByCustomer", "getOrdersByCustomer: $result")
            result.catch {
                _mutableOrdersList.value = ResponseState.Failure(it)
            }.collect{
                _mutableOrdersList.value = ResponseState.Success(it)
            }
        }
    }
}