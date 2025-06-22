package com.example.m_commerce.presentation.authentication.signUp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.presentation.utils.ResponseState
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.*
import com.google.gson.JsonObject
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel@Inject constructor(
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _signUpState = MutableStateFlow<ResponseState>(ResponseState.Success(Unit))
    val signUpState: StateFlow<ResponseState> = _signUpState

    private val shopDomain = "and2-ism-mad45.myshopify.com"
    private val accessToken = "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"

    fun signUp(
        email: String,
        password: String,
        confirmPassword: String,
        firstName: String,
        lastName: String
    ) {
        Log.d("SignUpViewModel", "Attempting sign up for email: $email")

        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank() ||
            firstName.isBlank() || lastName.isBlank()) {
            Log.d("SignUpViewModel", "Sign up failed: Missing fields")
            _signUpState.value = ResponseState.Failure(Throwable("Please fill in all fields"))
            return
        }

        if (password != confirmPassword) {
            Log.d("SignUpViewModel", "Sign up failed: Passwords do not match")
            _signUpState.value = ResponseState.Failure(Throwable("Passwords do not match"))
            return
        }

        _signUpState.value = ResponseState.Loading
        Log.d("SignUpViewModel", "Creating user with FirebaseAuth")

        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("SignUpViewModel", "Firebase user created successfully")
                        auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener { verifyTask ->
                                if (verifyTask.isSuccessful) {
                                    Log.d("SignUpViewModel", "Verification email sent")
                                    val profileUpdates = userProfileChangeRequest {
                                        displayName = "$firstName $lastName"
                                    }
                                    auth.currentUser?.updateProfile(profileUpdates)

                                    createShopifyCustomer(email, firstName, lastName)


                                } else {
                                    Log.d("SignUpViewModel", "Failed to send verification email")
                                    _signUpState.value = ResponseState.Failure(Throwable("Failed to send verification email"))
                                }
                            }
                    } else {
                        val errorMessage = when {
                            task.exception?.message == null -> "Sign up failed. Please try again."
                            task.exception?.message!!.contains("The email address is already in use") ->
                                "This email is already registered. Please use a different email."
                            else -> "Sign up failed. Please check your input and try again."
                        }
                        Log.d("SignUpViewModel", "Sign up failed: $errorMessage")
                        _signUpState.value = ResponseState.Failure(Throwable(errorMessage))
                    }
                }
        }
    }

    private fun createShopifyCustomer(email: String, firstName: String, lastName: String) {
        Log.d("SignUpViewModel", "Creating Shopify customer for $email")

        val client = OkHttpClient()

        val mutation = """
            mutation {
              customerCreate(input: {
                email: "$email"
                firstName: "$firstName"
                lastName: "$lastName"
              }) {
                customer {
                  id
                  email
                  firstName
                  lastName
                }
                userErrors {
                  field
                  message
                }
              }
            }
        """.trimIndent()

        val json = JsonObject().apply {
            addProperty("query", mutation)
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://$shopDomain/admin/api/2024-10/graphql.json")
            .addHeader("X-Shopify-Access-Token", accessToken)
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    client.newCall(request).execute().use { response ->
                        val responseBody = response.body?.string() ?: ""
                        if (!response.isSuccessful) {
                            Log.d("SignUpViewModel", "Shopify customer creation failed: HTTP ${response.code}")
                            _signUpState.value = ResponseState.Failure(Throwable("Failed to create Shopify customer: ${response.code}"))
                            return@use
                        }

                        val gson = Gson()
                        val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)
                        val data = jsonObject.get("data")?.takeIf { it.isJsonObject }?.asJsonObject
                        val customerCreate = data?.get("customerCreate")?.takeIf { it.isJsonObject }?.asJsonObject
                        val customer = customerCreate?.get("customer")?.takeIf { it.isJsonObject }?.asJsonObject
                        val userErrors = customerCreate?.get("userErrors")?.takeIf { it.isJsonArray }?.asJsonArray

                        if (customer != null) {
                            val id = customer.get("id")?.asString ?: "No ID"
                            val firstName = customer.get("firstName")?.asString ?: ""
                            val lastName = customer.get("lastName")?.asString ?: ""
                            val email = customer.get("email")?.asString ?: ""

                            val customerInfo = "Customer created: ID=$id, $firstName $lastName ($email)"
                            Log.d("SignUpViewModel", customerInfo)
                            sharedPreferencesHelper.saveCustomerId(id)
                            withContext(Dispatchers.Main) {
                                _signUpState.value = ResponseState.Success(customerInfo)
                            }
                        } else if (userErrors != null && userErrors.size() > 0) {
                            val messages = userErrors.joinToString("\n") {
                                it.asJsonObject.get("message").asString
                            }
                            Log.d("SignUpViewModel", "Shopify user errors: $messages")
                            withContext(Dispatchers.Main) {
                                _signUpState.value = ResponseState.Failure(Throwable("Shopify Error: $messages"))
                            }
                        } else {
                            Log.d("SignUpViewModel", "Unknown error creating Shopify customer")
                            Log.d("SignUpViewModel", "Response Body: $responseBody")
                            withContext(Dispatchers.Main) {
                                _signUpState.value = ResponseState.Failure(Throwable("Unknown error creating customer"))
                            }
                        }

                    }
                }
            } catch (e: Exception) {
                Log.e("SignUpViewModel", "Unexpected error", e)
                _signUpState.value = ResponseState.Failure(e)
            }
        }
    }

}

