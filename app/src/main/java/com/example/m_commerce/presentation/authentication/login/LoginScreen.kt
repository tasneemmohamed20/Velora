package com.example.m_commerce.presentation.authentication.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce.R
import com.example.m_commerce.presentation.utils.Functions.getFriendlyErrorMessage
import com.example.m_commerce.presentation.utils.ResponseState
import com.example.m_commerce.presentation.utils.theme.Primary

@Composable
fun LoginScreen(
    onButtonClicked: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val loginState by viewModel.loginState.collectAsState()
    var isLoginClicked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(R.drawable.logo_name),
            contentDescription = "app logo",
            modifier = Modifier.padding(bottom = 60.dp).fillMaxWidth(),
            alignment = Alignment.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text("Login", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Primary)
        Spacer(modifier = Modifier.height(40.dp))
        Text("Welcome back!", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.login(email, password)
                isLoginClicked = true
               },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (loginState is ResponseState.Loading && isLoginClicked) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text("Login")
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Don't have an account?")
            TextButton(onClick = onButtonClicked) {
                Text("Sign Up", color = Primary)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (loginState) {
            is ResponseState.Success -> {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, (loginState as ResponseState.Success).data as String, Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                }
            }
            is ResponseState.Failure -> {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, getFriendlyErrorMessage((loginState as ResponseState.Failure).err) , Toast.LENGTH_SHORT).show()
                }
            }
            else -> Unit
        }
    }
}
