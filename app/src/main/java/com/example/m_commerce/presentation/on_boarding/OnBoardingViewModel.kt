package com.example.m_commerce.presentation.on_boarding

import androidx.lifecycle.ViewModel
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : ViewModel() {

    fun completeOnboarding() {
        sharedPreferencesHelper.markUserAsNotFirstTime()
    }
}
