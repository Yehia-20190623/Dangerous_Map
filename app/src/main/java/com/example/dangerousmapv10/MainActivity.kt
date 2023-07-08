@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.example.dangerousmapv10

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Contacts.Intents.UI
import android.provider.Settings
import android.text.BoringLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.dangerousmapv10.Map.*
import com.example.dangerousmapv10.ui.theme.DangerousMapV10Theme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.composable
import com.example.dangerousmapv10.Authantication.LoginPage
import com.example.dangerousmapv10.Authantication.Register
import com.example.dangerousmapv10.data.Point
import com.example.dangerousmapv10.data.Role
import com.example.dangerousmapv10.ui.theme.DarkBlue
import com.example.dangerousmapv10.ui.theme.LightBlue
import com.google.accompanist.permissions.ExperimentalPermissionsApi

var points:List<Point>?=null
var isAdmin: Role = Role.ADMIN
var isLoggedIn:Boolean=false
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
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    //var drawerState = rememberDrawerState(initialValue =  DrawerValue.Closed)
                    var drawerState by remember {
                        mutableStateOf(DrawerState(initialValue = DrawerValue.Closed))
                    }


                    /*dialogQueue.forEach { permission ->
                        PermissionDialog(
                            permissionTextProvider = when (permission) {

                                Manifest.permission.CAMERA -> {
                                    CameraPermissionTextProvider()
                                }

                                Manifest.permission.ACCESS_FINE_LOCATION -> {
                                    LocationPermissionTextProvider()
                                }

                                Manifest.permission.ACCESS_COARSE_LOCATION -> {
                                    LocationPermissionTextProvider()
                                }

                                else -> return@forEach

                            },
                            isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                                permission
                            ),
                            onDismiss = viewModel::dismissDialog,
                            onOkClick = {
                                viewModel.dismissDialog()
                                multiplePermissionResultLauncher.launch(
                                    permissionsToRequest
                                )
                            },
                            onGoToAppSettingsClick = ::openAppSettings
                        )
                    }*/
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
                                onClick = { })
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
                                //LoginPage()
                                //Register()
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
        val text: String
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




    



