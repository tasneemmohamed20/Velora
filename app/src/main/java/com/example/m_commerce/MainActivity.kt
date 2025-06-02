package com.example.m_commerce

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.apollographql.apollo.ApolloClient
import com.example.m_commerce.ui.theme.MCommerceTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.i(TAG, "onCreate: ")
        GlobalScope.launch {
            val apolloClient = ApolloClient.Builder()
                .serverUrl("https://and2-ism.myshopify.com/admin/api/2025-04/graphql.json")
                .addHttpHeader("X-Shopify-Access-Token", "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")
                .addHttpHeader("Content-Type", "application/json")
                .build()
            try {
                val response = apolloClient.query(GetProductsQuery()).execute()
                val products = response.data?.products?.edges?.map { it?.node }
                products?.forEach {
                    Log.d(TAG, it?.title ?: "No title")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed: ${e.message}")
            }
        }
        setContent {
            MCommerceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MCommerceTheme {
        Greeting("Android")
    }
}