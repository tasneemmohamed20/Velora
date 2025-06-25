package com.example.m_commerce.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<SplashNavigationEvent>()
    val navigationEvent: SharedFlow<SplashNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        startSplashTimer()
    }

    private fun startSplashTimer() {
        viewModelScope.launch {
            delay(3000) // 3 seconds
            if (sharedPreferencesHelper.isFirstTimeUser()) {
                _navigationEvent.emit(SplashNavigationEvent.NavigateToOnBoarding)
            } else {
                _navigationEvent.emit(SplashNavigationEvent.NavigateToStart)
            }
        }
    }
}

sealed class SplashNavigationEvent {
    object NavigateToStart : SplashNavigationEvent()
    object NavigateToOnBoarding : SplashNavigationEvent()
}
