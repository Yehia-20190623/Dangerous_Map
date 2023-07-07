@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.dangerousmapv10

import android.widget.Toast
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dangerousmapv10.ui.theme.Black

@ExperimentalMaterial3Api
@Composable
fun AddPointToMap(navController: NavController) {
    val context = LocalContext.current
    val problemtype = arrayOf("hole", "bump", "work area")
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(problemtype[0]) }
    val problemlevel = arrayOf("high", "medium", "low")
    var selectedlevel by remember { mutableStateOf(problemlevel[0]) }
    var expanded1 by remember { mutableStateOf(false) }
    var discription by remember { mutableStateOf("") }
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
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                TextField(

                    value = selectedText,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = !expanded) },

                    )
                ExposedDropdownMenu(
                    expanded = expanded,

                    onDismissRequest = { expanded = false }

                ) {
                    problemtype.forEach { problem ->
                        DropdownMenuItem(

                            text = { Text(text = problem) },
                            onClick = {
                                selectedText = problem
                                expanded = false
                                Toast.makeText(context, problem, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
            ExposedDropdownMenuBox(
                expanded = expanded1,
                onExpandedChange = {
                    expanded1 = !expanded1
                }
            ) {
                TextField(

                    value = selectedlevel,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = !expanded1) },

                    )
                ExposedDropdownMenu(
                    expanded = expanded1,
                    onDismissRequest = { expanded1 = false }
                ) {
                    problemlevel.forEach { problem ->
                        DropdownMenuItem(

                            text = { Text(text = problem) },
                            onClick = {
                                selectedlevel = problem
                                expanded1 = false
                                Toast.makeText(context, problem, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
            val labelWidth = 100.dp
            Spacer(modifier = Modifier.height(50.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {

                //disc
                TextField(

                    value = discription,
                    onValueChange = { discription = it },
                    label = { Text("Discription") },
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
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = {

                navController.navigate("map")

            },

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E3C72),
                contentColor = Color.White
            ),


            ) {
            Text(text = "submit", fontSize = 24.sp)
        }


    }
}
