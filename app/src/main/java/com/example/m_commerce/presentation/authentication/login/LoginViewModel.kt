package com.example.m_commerce.presentation.authentication.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.ResponseState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _loginState = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val loginState: StateFlow<ResponseState> = _loginState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = ResponseState.Failure(Throwable("Please enter both email and password"))
            return
        }

        _loginState.value = ResponseState.Loading

        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user?.isEmailVerified == true) {
                            _loginState.value = ResponseState.Success("Welcome back! You have successfully logged in")
                        } else {
                            auth.signOut()
                            _loginState.value = ResponseState.Failure(Throwable("Your email is not verified yet. Please check your inbox and verify your email to proceed"))
                        }
                    } else {
                        val errorMessage = when {
                            task.exception?.message == null -> "Login failed Please try again"
                            task.exception?.message!!.contains("no user record") ->
                                "No account found with this email"
                            task.exception?.message!!.contains("password is invalid") ->
                                "Incorrect password. Please try again"
                            else -> "Login failed Please check your credentials and try again"
                        }

                        _loginState.value = ResponseState.Failure(Throwable(errorMessage))
                    }
                }
        }
    }
}
