//package com.example.m_commerce.presentation.account.settings.view_model
//
//import androidx.compose.runtime.mutableStateOf
//import androidx.lifecycle.ViewModel
//import com.example.m_commerce.domain.entities.Address
//import com.example.m_commerce.presentation.account.settings.view.Address
//import com.example.m_commerce.presentation.account.settings.view.AddressType
//
//class AddressesViewModel : ViewModel() {
//    private val _addresses = mutableStateOf<List<Address>>(emptyList())
//    val addresses = _addresses
//
//    private val _isBottomSheetVisible = mutableStateOf(false)
//    val isBottomSheetVisible = _isBottomSheetVisible
//
//    fun addAddress(city: String, building: String, apartment: String, floor: String, type: AddressType) {
//        val newAddress = Address(type, city, building, apartment, floor)
//        _addresses.value = _addresses.value + newAddress
//        hideBottomSheet()
//    }
//
//    fun showBottomSheet() {
//        _isBottomSheetVisible.value = true
//    }
//
//    fun hideBottomSheet() {
//        _isBottomSheetVisible.value = false
//    }
//}