package com.example.m_commerce.presentation.authentication.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_commerce.ResponseState
import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.example.m_commerce.domain.usecases.DraftOrderUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class LoginViewModel  @Inject constructor(
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val draftOrderUseCase: DraftOrderUseCase
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val _loginState = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val loginState: StateFlow<ResponseState> = _loginState

    private val shopDomain = "and2-ism-mad45.myshopify.com"
    private val accessToken = "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"

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
                            fetchShopifyCustomerId(email)
                            _loginState.value = ResponseState.Success("Welcome back! You have successfully logged in")
                            val customerEmail = sharedPreferencesHelper.getCustomerEmail()
                            viewModelScope.launch {
                                customerEmail?.let {
                                    try {
                                        getDraftOrder(it).let { hasExistingOrder ->
                                            if (hasExistingOrder) {
                                                Log.d("LoginViewModel", "Draft order already exists for customer: $customerEmail")
                                            } else {
                                                Log.d("LoginViewModel", "No existing draft order found for customer: $customerEmail")
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Log.e("LoginViewModel", "Error checking draft order", e)
                                    }
                                }
                            }
                        } else {
                            auth.signOut()
                            _loginState.value = ResponseState.Failure(
                                Throwable("Your email is not verified yet Please check your inbox and verify your email to proceed")
                            )
                        }
                    } else {
                        val errorMessage = when {
                            task.exception?.message == null -> "Login failed. Please try again"
                            task.exception?.message!!.contains("no user record") ->
                                "No account found with this email"
                            task.exception?.message!!.contains("password is invalid") ->
                                "Incorrect password. Please try again"
                            else -> "Login failed. Please check your credentials and try again"
                        }

                        _loginState.value = ResponseState.Failure(Throwable(errorMessage))
                    }
                }
        }
    }

    private fun fetchShopifyCustomerId(email: String) {
        val client = OkHttpClient()

        val query = """
            query {
              customers(first: 1, query: "email:$email") {
                edges {
                  node {
                    id
                    email
                    firstName
                    lastName
                  }
                }
              }
            }
        """.trimIndent()

        val json = JsonObject().apply {
            addProperty("query", query)
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
                        val gson = Gson()
                        val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)
                        val customers = jsonObject
                            .getAsJsonObject("data")
                            .getAsJsonObject("customers")
                            .getAsJsonArray("edges")

                        if (customers.size() > 0) {
                            val customer = customers[0].asJsonObject
                                .getAsJsonObject("node")
                            val customerId = customer.get("id").asString
                            val customerEmail = customer.get("email").asString
                            sharedPreferencesHelper.saveCustomerId(customerId)
                            sharedPreferencesHelper.saveCustomerEmail(customerEmail)
                            Log.d("LoginViewModel", "Full customer data: ${customer.toString()}")
                        }
                        else {
                            Log.d("LoginViewModel", "No Shopify customer found for email: $email")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("LoginError", "Failed to fetch Shopify customer ID", e)
            }
        }
    }

    private suspend fun getDraftOrder(customerId: String): Boolean {
        var hasExistingOrder = false
        try {
            Log.d("LoginViewModel", "Fetching draft orders for customer ID: $customerId")
            draftOrderUseCase(customerId)?.collect { draftOrders ->
                Log.d("LoginViewModel", "Draft orders: ${draftOrders}")
                draftOrders.find { it.note2 == "cart" }?.let { draftOrder ->
                    hasExistingOrder = true
                    Log.d("LoginViewModel", "Found existing draft order with note: ${draftOrder.note2}")
                    Log.d("LoginViewModel", "Draft order ID: ${draftOrder.id}")
                    sharedPreferencesHelper.saveCartDraftOrderId(draftOrder.id.toString())
                }
            }
        } catch (e: Exception) {

            Log.e("LoginViewModel", "Error getting draft orders", e)
        }
        return hasExistingOrder
    }
}
