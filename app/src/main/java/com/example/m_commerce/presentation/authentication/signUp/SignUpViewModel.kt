package com.example.m_commerce.presentation.authentication.signUp


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.ResponseState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _signUpState = MutableStateFlow<ResponseState>(ResponseState.Success(Unit))
    val signUpState: StateFlow<ResponseState> = _signUpState

    fun signUp(email: String, password: String, confirmPassword: String) {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _signUpState.value = ResponseState.Failure(Throwable("Please fill in all fields"))
            return
        }

        if (password != confirmPassword) {
            _signUpState.value = ResponseState.Failure(Throwable("Passwords do not match"))
            return
        }

        _signUpState.value = ResponseState.Loading

        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener { verifyTask ->
                                if (verifyTask.isSuccessful) {
                                    _signUpState.value = ResponseState.Success("Verification email sent Please check your inbox")
                                } else {
                                    _signUpState.value = ResponseState.Failure(Throwable("Failed to send verification email Please try again later"))
                                }
                            }
                    } else {
                        val errorMessage = when {
                            task.exception?.message == null -> "Sign up failed Please try again"
                            task.exception?.message!!.contains("The email address is already in use") ->
                                "This email is already registered Please use a different email"
                            else -> "Sign up failed Please check your input and try again"
                        }
                        _signUpState.value = ResponseState.Failure(Throwable(errorMessage))
                    }
                }
        }
    }
}
