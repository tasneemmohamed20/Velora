package com.example.m_commerce.start

import androidx.lifecycle.ViewModel
import com.example.m_commerce.ResponseState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StartViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _googleSignInState = MutableStateFlow<ResponseState?>(null)
    val googleSignInState: StateFlow<ResponseState?> = _googleSignInState

    fun handleGoogleSignIn(idToken: String) {
        _googleSignInState.value = ResponseState.Loading
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                _googleSignInState.value = ResponseState.Success(it)
            }
            .addOnFailureListener {
                _googleSignInState.value = ResponseState.Failure(it)
            }
    }

    fun clearGoogleSignInState() {
        _googleSignInState.value = null
    }
}
