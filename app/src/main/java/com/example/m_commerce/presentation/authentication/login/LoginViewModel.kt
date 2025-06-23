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
import kotlinx.coroutines.tasks.await
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

        viewModelScope.launch {
            try {
                _loginState.value = ResponseState.Loading

                val authResult = withContext(Dispatchers.IO) {
                    auth.signInWithEmailAndPassword(email, password).await()
                }

                val user = authResult.user
                if (user?.isEmailVerified != true) {
                    auth.signOut()
                    _loginState.value = ResponseState.Failure(
                        Throwable("Please verify your email first")
                    )
                    return@launch
                }

                sharedPreferencesHelper.saveCustomerEmail(email)

                val shopifyJob = viewModelScope.launch(Dispatchers.IO) {
                    try {
                        fetchShopifyCustomerIdSuspend(email)
                    } catch (e: Exception) {
                        Log.e("LoginViewModel", "Shopify fetch failed", e)
                    }
                }

                val draftOrderJob = viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val hasOrder = getDraftOrder(email)
                        Log.d("LoginViewModel",
                            if (hasOrder) "Draft order exists for: $email"
                            else "No draft order for: $email"
                        )
                    } catch (e: Exception) {
                        Log.e("LoginViewModel", "Draft order check failed", e)
                    }
                }

                // Update UI state immediately after Firebase auth
                _loginState.value = ResponseState.Success("Welcome back!")

                // Wait for background tasks to complete
                shopifyJob.join()
                draftOrderJob.join()

            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("no user record") == true -> "No account found"
                    e.message?.contains("password is invalid") == true -> "Incorrect password"
                    else -> "Login failed: ${e.message}"
                }
                _loginState.value = ResponseState.Failure(Throwable(errorMessage))
            }
        }
    }

    private suspend fun fetchShopifyCustomerIdSuspend(email: String) = withContext(Dispatchers.IO) {
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

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("API call failed with code: ${response.code}")
            }

            val responseBody = response.body?.string() ?: ""
            Log.d("LoginViewModel", "Raw response: $responseBody")

            val gson = Gson()
            val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)

            if (jsonObject.has("errors")) {
                throw Exception("GraphQL query failed")
            }

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

                Log.d("LoginViewModel", "Saved customer data - ID: $customerId, Email: $customerEmail")
            }
        }
    }

    private suspend fun getDraftOrder(customerId: String): Boolean {
//        var hasExistingOrder = false
        var hasCartOrder = false
        var hasFavOrder = false
        try {
            Log.d("LoginViewModel", "Fetching draft orders for customer ID: $customerId")
            draftOrderUseCase(customerId)?.collect { draftOrders ->
                Log.d("LoginViewModel", "Draft orders: ${draftOrders}")
                when (draftOrders.note2) {
                    "cart" -> {
                        hasCartOrder = true
                        Log.d("LoginViewModel", "Found cart draft order: ${draftOrders.id}")
                        sharedPreferencesHelper.saveCartDraftOrderId(draftOrders.id.toString())
                    }
                    "fav" -> {
                        hasFavOrder = true
                        Log.d("LoginViewModel", "Found favorite draft order: ${draftOrders.id}")

                    }
                }
            }
        } catch (e: Exception) {

            Log.e("LoginViewModel", "Error getting draft orders", e)
        }
        return hasCartOrder || hasFavOrder
    }
}
