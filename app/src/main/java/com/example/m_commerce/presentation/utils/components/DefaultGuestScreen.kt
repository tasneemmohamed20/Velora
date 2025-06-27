package com.example.m_commerce.presentation.utils.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.m_commerce.R
import com.example.m_commerce.presentation.utils.theme.Primary

@Composable
fun DefaultGuestScreen(
    onLoginClicked: () -> Unit,
    description: String
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.velora_logo), contentDescription = "No Orders Guest")
        Spacer(Modifier.height(18.dp))
        Text(
            text = "Hey There!",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.titleSmall,
            color = Color.Black,
            textAlign = TextAlign.Center,
            maxLines = 3
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onLoginClicked,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = Color.White
            )
        ){
            Text(
                text = "Log in",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}