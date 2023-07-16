package com.example.dangerousmapv10.Authantication

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.example.dangerousmapv10.API.SystemApiInterface
import com.example.dangerousmapv10.ApplicationAdmin
import com.example.dangerousmapv10.R
import com.example.dangerousmapv10.data.User
import com.example.dangerousmapv10.isAdmin
import com.example.dangerousmapv10.isUser
import com.example.dangerousmapv10.localhost

import com.example.dangerousmapv10.mAuth
import com.example.dangerousmapv10.ui.theme.Black
import com.example.dangerousmapv10.userApplication
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


@Composable
fun LoginPage(nav: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val adminEmail = "admin@gmail.com"

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
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)


                    .size(150.dp)
                    .clip(CircleShape)

            )
            Spacer(modifier = Modifier.height(50.dp))
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
                    text = "Email: ",
                    color = Color.White,
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
            Text(
                text = "Don't have Account?",
                color = Color.White,
                modifier = Modifier
                    .clickable {
                        nav.navigate("register")
                    }
                    .align(Alignment.CenterHorizontally)
            )
        }
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
                } else {
                    mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                //Toast.makeText(context, "Login Successfully", Toast.LENGTH_LONG).show()
                                val id = mAuth.currentUser!!.uid
                                if (email.equals(adminEmail)) {
                                    isAdmin.value = true

                                    GlobalScope.launch(Dispatchers.IO) {

                                        // Use adminUser object as needed
                                        MainScope().launch {
                                            Toast.makeText(
                                                context,
                                                "Hello Admin",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                } else {

                                    isUser = true
                                    GlobalScope.launch(Dispatchers.IO) {
                                        val result = getUser(id)
                                        // Use adminUser object as needed
                                        userApplication = User()
                                        userApplication!!.name = result!!.name
                                        userApplication!!.email = result!!.email
                                        userApplication!!.password = result!!.password
                                        userApplication!!.role = result!!.role
                                        MainScope().launch {
                                            Toast.makeText(
                                                context,
                                                "Hello " + result!!.name,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                                nav.navigateUp()
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(
                                    context,
                                    "Email or password is invalid" + task.exception,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

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
            Text(text = "Login", fontSize = 24.sp)
        }
    }
}

suspend fun getUser(id: String): User? {
    return suspendCoroutine { continuation ->
        val retrofit = Retrofit.Builder()
            .baseUrl("http://$localhost:8080/system/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface: SystemApiInterface = retrofit.create(SystemApiInterface::class.java)
        val call = apiInterface.getUser(id)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    continuation.resume(response.body())
                } else {
                    continuation.resume(null)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }
}
