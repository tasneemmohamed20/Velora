package com.example.m_commerce.start

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.presentation.utils.ResponseState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject


class StartViewModel @Inject constructor() : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _googleSignInState = MutableStateFlow<ResponseState?>(null)
    val googleSignInState: StateFlow<ResponseState?> = _googleSignInState

    private val shopDomain = "and2-ism-mad45.myshopify.com"
    private val accessToken = "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"

    fun handleGoogleSignIn(idToken: String) {
        _googleSignInState.value = ResponseState.Loading
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val user = result.user
                val name = user?.displayName ?: "Google User"
                val email = user?.email ?: ""

                Log.d("GoogleSignIn", "Signed in as: $name <$email>")

                createShopifyCustomer(name, email)
            }
            .addOnFailureListener {
                Log.e("GoogleSignIn", "Sign in failed", it)
                _googleSignInState.value = ResponseState.Failure(it)

            }
    }

    private fun createShopifyCustomer(name: String, email: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val client = OkHttpClient()

                    val customerJson = """
                    {
                        "customer": {
                            "first_name": "${name}",
                            "email": "${email}",
                            "verified_email": true,
                            "tags": "GoogleSignIn"
                        }
                    }
                """.trimIndent()

                    val requestBody = customerJson.toRequestBody("application/json".toMediaTypeOrNull())

                    val request = Request.Builder()
                        .url("https://$shopDomain/admin/api/2024-04/customers.json")
                        .addHeader("X-Shopify-Access-Token", accessToken)
                        .post(requestBody)
                        .build()

                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string()

                    Log.d("ShopifyAPI", "Response Code: ${response.code}")
                    Log.d("ShopifyAPI", "Response Body: $responseBody")

                    if (response.isSuccessful && responseBody != null) {
                        ResponseState.Success(responseBody)
                    } else if (response.code == 422 && responseBody?.contains("email") == true) {
                        ResponseState.Success("Email already taken user can proceed")
                    } else {
                        ResponseState.Failure(Throwable("Shopify API Error: ${response.code} ${responseBody}"))
                    }
                } catch (e: Exception) {
                    ResponseState.Failure(e)
                }
            }

            _googleSignInState.value = result
        }
    }

    fun clearGoogleSignInState() {
        _googleSignInState.value = null
    }
}
