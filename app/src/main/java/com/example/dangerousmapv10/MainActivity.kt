@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.dangerousmapv10

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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
import androidx.navigation.compose.composable
import com.example.dangerousmapv10.Location.LocationService

class MainActivity : ComponentActivity() {
    private val permissionsToRequest = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DangerousMapV10Theme {
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
                        Scaffold(topBar = {
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
                            Map(Modifier.padding(contentPadding), navController = rememberNavController())
                            multiplePermissionResultLauncher.launch(permissionsToRequest)
                            nav()
                        }
                    }
                }
            }
        }
    }
}

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
fun nav(){
    val navController= rememberNavController()
    NavHost(navController = navController, startDestination ="map"  ){
        composable(route ="addpoint"){
            AddPointToMap(navController)
        }
        composable(route ="map"){
            Map(modifier = Modifier,navController)

        }
    }

}
@Preview(showBackground = true)
@Composable
fun MapPreview() {
    DangerousMapV10Theme {
        Map(modifier = Modifier,navController= rememberNavController())
    }
}





    



