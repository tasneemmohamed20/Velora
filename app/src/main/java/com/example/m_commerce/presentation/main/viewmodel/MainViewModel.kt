package com.example.m_commerce.presentation.main.viewmodel

import androidx.lifecycle.ViewModel
import com.example.m_commerce.presentation.utils.ConnectivityHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val connectivityHelper: ConnectivityHelper
) : ViewModel() {
    val isConnected: StateFlow<Boolean> = connectivityHelper.isConnected
}
