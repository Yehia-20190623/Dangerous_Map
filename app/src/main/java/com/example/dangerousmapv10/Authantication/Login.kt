package com.example.dangerousmapv10.Authantication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dangerousmapv10.ui.theme.Black


@Composable
fun LoginPage() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Login",
                color = Color.Black,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(32.dp))
            val labelWidth = 80.dp
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Email: ",
                    color = Color.Black,
                    modifier = Modifier.width(labelWidth)
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Black,
                        containerColor = Color(0xFFD3D3D3),
                        unfocusedLabelColor = Color(0xFF696969),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Password: ",
                    color = Color.Black,
                    modifier = Modifier.width(labelWidth)
                )
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Black,
                        containerColor = Color(0xFFD3D3D3),
                        unfocusedLabelColor = Color(0xFF696969),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }
        Button(
            onClick = {
                print(email)
                print(password)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E3C72),
                contentColor = Color.White
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .height(60.dp)

        ) {
            Text(text = "Login", fontSize = 24.sp)
        }
    }
}
