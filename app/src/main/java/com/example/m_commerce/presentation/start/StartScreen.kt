package com.example.m_commerce.presentation.start

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.m_commerce.R
import com.example.m_commerce.presentation.utils.ResponseState
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce.presentation.utils.theme.Primary
import com.example.m_commerce.start.StartViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.launch

@Composable
fun StartScreen(
    onEmailClicked: () -> Unit,
    onGoogleSuccess: () -> Unit,
    onGuestSuccess: () -> Unit,
    viewModel: StartViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val signInState by viewModel.googleSignInState.collectAsStateWithLifecycle()
    val guestModeState by viewModel.guestModeState.collectAsStateWithLifecycle()
    val webClientId = stringResource(id = R.string.web_client_id)

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    viewModel.handleGoogleSignIn(idToken)
                }
            } catch (_: ApiException) {}
        }
    }

    val googleSignInClient = remember(webClientId) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    LaunchedEffect(signInState) {
        when (signInState) {
            is ResponseState.Success -> {
                viewModel.clearGoogleSignInState()
                onGoogleSuccess()
            }
            is ResponseState.Failure -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Sign-in failed. Please try again.",
                        withDismissAction = true
                    )
                }
            }
            else -> Unit
        }
    }

    LaunchedEffect(guestModeState) {
        when (guestModeState) {
            is ResponseState.Success -> {
                viewModel.clearGuestModeState()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Welcome, Guest!",
                        withDismissAction = true
                    )
                }
                onGuestSuccess()
            }
            is ResponseState.Failure -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Failed to enter guest mode. Please try again.",
                        withDismissAction = true
                    )
                }
            }
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(state = scrollState)
        ) {
            Spacer(modifier = Modifier.height(120.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "App Logo",
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.velora_title),
                contentDescription = "App Logo",
                modifier = Modifier.width(200.dp)
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Title and subtitle texts
            Text(
                text = "Log in or create an account",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Receive rewards and save your details for a faster checkout experience.",
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                CustomButton(
                    text = "Continue with Google",
                    icon = R.drawable.ic_google,
                    onClick = { launcher.launch(googleSignInClient.signInIntent) }
                )
                CustomButton(
                    text = "Continue with Email",
                    icon = R.drawable.ic_gmail,
                    onClick = onEmailClicked
                )
                CustomButton("Continue as Guest", R.drawable.ic_person) {
                    viewModel.handleGuestMode()
                }
            }
        }

        if (signInState is ResponseState.Loading || guestModeState is ResponseState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center),
                color = Primary
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun CustomButton(text: String, icon: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.2f)),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}