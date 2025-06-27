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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.m_commerce.domain.entities.payment.OrderItem
import com.example.m_commerce.presentation.utils.components.ErrorAlertDialog


@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel = hiltViewModel(),
    onPaymentComplete: () -> Unit,
    onPaymentError: (String) -> Unit,
    items: List<OrderItem>,
    totalAmountCents: Int,
    modifier: Modifier = Modifier
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
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
                val iframeUrl =
                    "https://accept.paymob.com/api/acceptance/iframes/933093?payment_token=${paymentKeyState?.token}"

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
                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    val url = request?.url.toString()
                                    Log.d("PaymentScreen", "URL: $url")

                                    if (url.contains("accept.paymobsolutions.com/api/acceptance/shopify_callback")) {
                                        val uri = Uri.parse(url)
                                        val isSuccess = uri.getQueryParameter("success") == "true"
                                        val txnResponse = uri.getQueryParameter("txn_response_code")

                                        if (isSuccess) {
                                            viewModel.completeDraftOrder()
                                        } else {
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

        if(viewModel.showErrorDialog.value){
            ErrorAlertDialog(message = "Failed to complete order. Please try again.", onDismiss = {viewModel.toggleErrorAlert()})
        }
        if (viewModel.showSuccessDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    viewModel.toggleSuccessAlert()
                    onPaymentComplete()
                },
                title = {
                    Text("🎉 Order Confirmed!", style = MaterialTheme.typography.titleLarge)
                },
                text = {
                    Text(
                        "Your order has been placed successfully. Thank you!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.toggleSuccessAlert()
                        onPaymentComplete()
                    }) {
                        Text("OK")
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = Color(0xFF4CAF50), // Green
                    )
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}