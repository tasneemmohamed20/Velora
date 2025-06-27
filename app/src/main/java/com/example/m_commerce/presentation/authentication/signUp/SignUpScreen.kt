package com.example.m_commerce.presentation.authentication.signUp

import android.widget.Toast
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.m_commerce.presentation.utils.ResponseState
import com.example.m_commerce.presentation.utils.theme.Primary

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SignUpScreen(
    onButtonClicked: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val signUpState by viewModel.signUpState.collectAsState()
    val successMessage = (signUpState as? ResponseState.Success)?.data as? String
    val errorMessage = (signUpState as? ResponseState.Failure)?.err?.message

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Sign Up", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Primary)
        Spacer(modifier = Modifier.height(40.dp))
        Text("Create your account", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("First Name") }
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Last Name") }
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            }
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (firstName.isBlank() || lastName.isBlank() || email.isBlank() ||
                    password.isBlank() || confirmPassword.isBlank()) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_LONG).show()
                    return@Button
                }

                Toast.makeText(context, "Email verification sent to your email", Toast.LENGTH_LONG).show()
                viewModel.signUp(email, password, confirmPassword, firstName, lastName)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Already have an account?")
            TextButton(onClick = onButtonClicked) {
                Text("Login", color = Primary)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (signUpState) {
            is ResponseState.Loading -> {
                CircularWavyProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            else -> Unit
        }

        LaunchedEffect(successMessage) {
            successMessage?.let {
                Toast.makeText(context, "Sign up successful! Please check your email for verification.", Toast.LENGTH_LONG).show()
            }
        }

        LaunchedEffect(errorMessage) {
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }
}