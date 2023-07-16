package com.example.dangerousmapv10.Authantication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.dangerousmapv10.API.SystemApiInterface
import com.example.dangerousmapv10.R
import com.example.dangerousmapv10.data.User
import com.example.dangerousmapv10.localhost
import com.example.dangerousmapv10.mAuth
import com.example.dangerousmapv10.ui.theme.Black
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@Composable
fun Register(nav:NavController) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordAgain by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1D3B71))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.dp)
        ) {
            Image(

                painter = painterResource(id = R.drawable.imj),
                contentDescription =null,
                modifier = Modifier.align(Alignment.CenterHorizontally)


                    .size(150.dp)
                    .clip(CircleShape)

            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Welcome Dangerous Map",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(32.dp))
            val labelWidth = 80.dp
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "username: ",
                    color = Color.White,
                    modifier = Modifier.width(labelWidth)
                )
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("username       ") },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Color.Black,
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
                    text = "Email: ",
                    color = Color.White,
                    modifier = Modifier.width(labelWidth)
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Color.Black,
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
                    color = Color.White,
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
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Password again: ",
                    color = Color.White,
                    modifier = Modifier.width(labelWidth)
                )
                TextField(
                    value = passwordAgain,
                    onValueChange = { passwordAgain = it },
                    label = { Text("Password again") },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Color.Black,
                        containerColor = Color(0xFFD3D3D3),
                        unfocusedLabelColor = Color(0xFF696969),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = {
                if (email.isEmpty() || password.isEmpty()) {
                    if (email.isEmpty()) {
                        Toast.makeText(context, "Please Enter The Email", Toast.LENGTH_SHORT).show()
                    }
                    if (password.isEmpty()) {
                        Toast.makeText(context, "Please Enter The Password", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
                else if (!(passwordAgain.equals(password))){
                    Toast.makeText(context, "Make sure password and password again is the same", Toast.LENGTH_SHORT)
                        .show()
                }
                else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
                            if (task.isSuccessful) {
                                //Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT).show()

                                val user = User()
                                user.email = email
                                user.password = password
                                user.name = username
                                user.role="USER"
                                user.id = mAuth.currentUser!!.uid
                                coroutineScope.launch {
                                    signUp(user)
                                }
                                nav.navigateUp()
                            } else {
                                // If sign in fails, display a message to the user.
                            }
                        })

                }
                },

            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF1D3B71)
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .height(60.dp)

        ) {
            Text(text = "Register", fontSize = 24.sp)
        }
    }
}
suspend fun signUp(user:User){
    val retrofit = Retrofit.Builder()
        .baseUrl("http://$localhost:8080/system/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val apiInterface: SystemApiInterface = retrofit.create(SystemApiInterface::class.java)
    val call: Call<Boolean> = apiInterface.signup(user)
    return suspendCoroutine { continuation ->
        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if(response.isSuccessful){
                    continuation.resume(Unit)
                }
            }
            override fun onFailure(call: Call<Boolean>, t: Throwable) {

            }
            })
        }
}