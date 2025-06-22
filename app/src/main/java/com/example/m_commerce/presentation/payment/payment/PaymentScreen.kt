package com.example.m_commerce.presentation.payment.payment

import android.net.Uri
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.m_commerce.domain.entities.payment.OrderItem


@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel = hiltViewModel(),
    onPaymentComplete: () -> Unit,
    onPaymentError: (String) -> Unit,
    items: List<OrderItem>,
    totalAmountCents: Int,
    modifier: Modifier = Modifier
)  {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val orderState by viewModel.orderState.collectAsStateWithLifecycle()
    val paymentKeyState by viewModel.paymentKeyState.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        authState?.let { auth ->
            Log.d("PaymentScreen", "Auth token received: ${auth.token}")
            viewModel.createOrder(totalAmountCents, items)
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            paymentKeyState != null -> {
                val iframeUrl = "https://accept.paymob.com/api/acceptance/iframes/933093?payment_token=${paymentKeyState?.token}"

                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                            }

                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                    val url = request?.url.toString()
                                    Log.d("PaymentScreen", "URL: $url")

                                    if (url.contains("accept.paymobsolutions.com/api/acceptance/post_pay")) {
                                        val uri = Uri.parse(url)
                                        val isSuccess = uri.getQueryParameter("success") == "true"
                                        val txnResponse = uri.getQueryParameter("txn_response_code")

                                        if (isSuccess && txnResponse == "APPROVED") {
                                            Log.d("PaymentScreen", "Payment successful")
                                            onPaymentComplete()
                                        } else {
                                            Log.d("PaymentScreen", "Payment failed: $txnResponse")
                                            onPaymentError("Payment failed: Transaction not approved")
                                        }
                                        return true
                                    }
                                    return false
                                }
                            }
                            loadUrl(iframeUrl)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            error != null -> {
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error
                )
                Button(onClick = { viewModel.createOrder(totalAmountCents, items) }) {
                    Text("Retry")
                }
            }
            else -> {
                CircularProgressIndicator()
                Text(
                    text = "Processing payment...",
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}