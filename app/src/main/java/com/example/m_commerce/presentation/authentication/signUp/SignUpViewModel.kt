package com.example.m_commerce.presentation.authentication.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.presentation.utils.ResponseState
import com.example.m_commerce.domain.usecases.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : ViewModel() {

    private val _signUpState = MutableStateFlow<ResponseState>(ResponseState.Success(Unit))
    val signUpState: StateFlow<ResponseState> = _signUpState

    fun signUp(
        email: String,
        password: String,
        confirmPassword: String,
        firstName: String,
        lastName: String
    ) {

        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank() ||
            firstName.isBlank() || lastName.isBlank()
        ) {
            _signUpState.value = ResponseState.Failure(Throwable("Please fill in all fields"))
            return
        }

        if (password != confirmPassword) {
            _signUpState.value = ResponseState.Failure(Throwable("Passwords do not match"))
            return
        }

        _signUpState.value = ResponseState.Loading

        viewModelScope.launch {
            val result = authUseCase.signUp(email, password, firstName, lastName)
            result.fold(
                onSuccess = {
                    sharedPreferencesHelper.saveCustomerEmail(email)
                    _signUpState.value = ResponseState.Success("Account created successfully")
                },
                onFailure = { error ->
                    _signUpState.value = ResponseState.Failure(error)
                }
            )
        }
    }
}