package com.example.m_commerce.data.datasource.remote.graphql.auth

import com.example.m_commerce.data.datasource.local.SharedPreferencesHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : AuthDataSource {

    private val shopDomain = "and2-ism-mad45.myshopify.com"
    private val accessToken = "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"
    private val client = OkHttpClient()

    override suspend fun signUp(email: String, password: String, firstName: String, lastName: String): Result<Unit> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = firebaseAuth.currentUser
            user?.sendEmailVerification()?.await()
            val profileUpdates = userProfileChangeRequest {
                displayName = "$firstName $lastName"
            }
            user?.updateProfile(profileUpdates)?.await()

            val shopifyResult = createShopifyCustomer(email, firstName, lastName)
            if (shopifyResult.isFailure) return shopifyResult

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = firebaseAuth.currentUser
            if (user?.isEmailVerified != true) {
                firebaseAuth.signOut()
                return Result.failure(Throwable("Your email is not verified yet Please check your inbox and verify your email to proceed."))
            }
            val shopifyResult = fetchShopifyCustomerId(email)
            if (shopifyResult.isFailure) return shopifyResult

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createShopifyCustomer(email: String, firstName: String, lastName: String): Result<Unit> {
        return try {
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

            val json = JsonObject().apply { addProperty("query", mutation) }
            val body = json.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://$shopDomain/admin/api/2024-10/graphql.json")
                .addHeader("X-Shopify-Access-Token", accessToken)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            withContext(Dispatchers.IO) {
                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string() ?: ""
                    if (!response.isSuccessful) {
                        return@withContext Result.failure(Throwable("Failed to create Shopify customer: ${response.code}"))
                    }
                    val gson = Gson()
                    val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)
                    val data = jsonObject.getAsJsonObject("data")
                    val customerCreate = data.getAsJsonObject("customerCreate")
                    val customer = customerCreate.getAsJsonObject("customer")
                    val userErrors = customerCreate.getAsJsonArray("userErrors")

                    if (customer != null && customer.has("id")) {
                        val customerId = customer.get("id").asString
                        sharedPreferencesHelper.saveCustomerId(customerId)
                        Result.success(Unit)
                    } else if (userErrors != null && userErrors.size() > 0) {
                        val messages = userErrors.joinToString("\n") {
                            it.asJsonObject.get("message").asString
                        }
                        Result.failure(Throwable("Shopify Error: $messages"))
                    } else {
                        Result.failure(Throwable("Unknown error creating customer"))
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchShopifyCustomerId(email: String): Result<Unit> {
        return try {
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

            val json = JsonObject().apply { addProperty("query", query) }
            val body = json.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://$shopDomain/admin/api/2024-10/graphql.json")
                .addHeader("X-Shopify-Access-Token", accessToken)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

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
                        val customer = customers[0].asJsonObject.getAsJsonObject("node")
                        val customerId = customer.get("id").asString
                        sharedPreferencesHelper.saveCustomerId(customerId)
                        Result.success(Unit)
                    } else {
                        Result.failure(Throwable("No Shopify customer found for email: $email"))
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

