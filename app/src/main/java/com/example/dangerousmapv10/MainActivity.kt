@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.example.dangerousmapv10

import android.Manifest
import android.app.Activity
import android.app.Notification.Action
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dangerousmapv10.Authantication.LoginPage
import com.example.dangerousmapv10.Authantication.Register
import com.example.dangerousmapv10.Map.Map
import com.example.dangerousmapv10.data.Point
import com.example.dangerousmapv10.data.Role
import com.example.dangerousmapv10.location.LocationService
import com.example.dangerousmapv10.ui.theme.DangerousMapV10Theme
import com.example.dangerousmapv10.ui.theme.DarkBlue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


lateinit var points: List<Point>
var isAdmin: Role = Role.ADMIN
var isLoggedIn = mutableStateOf<Boolean>(false)
var nearestPoint: Point? = null
val mAuth = FirebaseAuth.getInstance()
var loggedInUser: FirebaseUser? = null


class MainActivity : ComponentActivity() {
    private val permissionsToRequest = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        //Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CAMERA
    )

    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DangerousMapV10Theme {
                val context = LocalContext.current
                Intent(applicationContext, LocationService::class.java).apply {
                    action = LocationService.ACTION_START
                    startService(this)
                }
                val viewModel = viewModel<MainViewModel>()
                val dialogQueue = viewModel.visiblePermissionDialogQueue
                val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = { perms ->
                        permissionsToRequest.forEach { permission ->
                            viewModel.onPermissionResult(
                                permission = permission,
                                isGranted = perms[permission] == true
                            )
                        }
                    }
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary
                ) {

                    var drawerState by remember {
                        mutableStateOf(DrawerState(initialValue = DrawerValue.Closed))
                    }

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContainerColor = DarkBlue,
                        drawerContentColor = Color.White,
                        drawerContent = {
                            NavigationDrawerItem(
                                label = {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Image(
                                            painter = (painterResource(id = R.drawable.baseline_close_24)),
                                            contentDescription = ""
                                        )
                                        Text(text = "Close")

                                    }
                                },

                                selected = false,
                                onClick = {
                                    drawerState = DrawerState(initialValue = DrawerValue.Closed)
                                })
                            Spacer(modifier = Modifier.height(8.dp))

                            NavigationDrawerItem(
                                label = {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Image(
                                            painter = (painterResource(id = R.drawable.baseline_settings_24)),
                                            contentDescription = ""
                                        )
                                        Text(text = "Settings")
                                    }

                                },
                                selected = false,
                                onClick = { })
                            Spacer(modifier = Modifier.height(8.dp))
                            NavigationDrawerItem(
                                label = {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Image(
                                            painter = (painterResource(id = R.drawable.baseline_share_24)),
                                            contentDescription = ""
                                        )
                                        Text(text = "Share")
                                    }
                                },
                                selected = false,
                                onClick = { })
                            Spacer(modifier = Modifier.height(8.dp))
                            if (isLoggedIn.value) {
                                NavigationDrawerItem(
                                    label = {
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Image(
                                                painter = (painterResource(id = R.drawable.baseline_logout_24)),
                                                contentDescription = ""
                                            )
                                            Text(text = "Log Out")
                                        }

                                    },
                                    selected = false,
                                    onClick = {
                                        isLoggedIn.value = false
                                        Toast.makeText(
                                            context,
                                            loggedInUser!!.email + " Logged out",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        FirebaseAuth.getInstance().signOut()
                                        loggedInUser = null
                                    })
                            } else {

                            }
                        },
                        gesturesEnabled = false,
                    ) {
                        Scaffold(containerColor = Color.Black,
                            topBar = {
                                SmallTopAppBar(
                                    title = { Text(text = "My app") },

                                    navigationIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "none",
                                            modifier = Modifier.clickable {
                                                drawerState =
                                                    DrawerState(initialValue = DrawerValue.Open)
                                            }
                                        )

                                    },
                                )
                            }) { contentPadding ->
                            if (applicationContext.hasLocationPermission()) {
                                Map(
                                    Modifier.padding(contentPadding),
                                    navController = rememberNavController()
                                )

                            }


                            multiplePermissionResultLauncher.launch(permissionsToRequest)
                            nav()
                        }
                    }
                }
            }
        }
    }
//}

    fun Activity.openAppSettings() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        ).also(::startActivity)
    }

    data class DropDownItem(
        val text: String,
    )

    @Composable
    fun nav() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "map") {
            composable(route = "addpoint") {
                AddPointToMap(navController)
            }
            composable(route = "map") {
                Map(modifier = Modifier, navController)

            }
            composable(route = "login") {
                LoginPage(navController)
            }
            composable(route = "register") {
                Register(navController)
            }
        }

    }

    @Preview(showBackground = true)
    @Composable
    fun MapPreview() {
        DangerousMapV10Theme {
            Map(modifier = Modifier, navController = rememberNavController())
        }
    }
}




    



