@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.dangerousmapv10

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.BoxScopeInstance.align
//import androidx.compose.foundation.layout.BoxScopeInstance.align
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.dangerousmapv10.API.APInterface
import com.example.dangerousmapv10.Data.Point
import com.example.dangerousmapv10.Map.*
import com.example.dangerousmapv10.ui.theme.Black
import com.example.dangerousmapv10.ui.theme.DangerousMapV10Theme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.composable
import com.example.dangerousmapv10.Location.LocationService
import dagger.hilt.android.AndroidEntryPoint


//import androidx.compose.ui.text.input.PasswordTextField


var locationManager: LocationManager? = null

@OptIn(ExperimentalMaterial3Api::class)

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
                                            painter = (painterResource(id = R.drawable.baseline_arrow_back_24)),
                                            contentDescription = ""
                                        )
                                        Text(text = "Back")

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

                            //Addpointonmap()
                            Map(Modifier.padding(contentPadding), navController = rememberNavController())
                            //Register()


                            //LoginPage()
                            multiplePermissionResultLauncher.launch(permissionsToRequest)
                            nav()
                        }
                    }

                    //

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
@Composable
fun showPoints(){
    val points= getPoints()
    if (points != null) {
        for (i in points){
            setMarker(lat = i.latitude, long = i.longitude)

        }
    }

}

fun getPoints(): List<Point>? {
    var points: List<Point>? = null
    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/map/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiInterface:APInterface = retrofit.create(APInterface::class.java)
    val call: Call<List<Point>> = apiInterface.getPoints()
    call.enqueue(object : Callback<List<Point>> {

        override fun onResponse(call: Call<List<Point>>, response: Response<List<Point>>) {
            print("Response returned")
            var result = ""
            points= response.body()
            for(i in points!!){
                print(i.latitude)
            }
        }

        override fun onFailure(call: Call<List<Point>>, t: Throwable) {
            print("Request failed")
        }
    })
    return points
}
@Composable
fun setMarker(lat:Double,long:Double){
    val point=LatLng(lat,long)
    Marker(state = MarkerState(point), title = "")

}

@Composable
fun addMarker(myloc: MarkerState) {

    Marker(state = myloc, title = "onClick")

}

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


@Preview(showBackground = true)
@Composable
fun MapPreview() {
    DangerousMapV10Theme {
        Map(modifier = Modifier,navController= rememberNavController())
    }
}
@Composable
fun Register() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
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
                text = "Regitser",
                color = Color.Black,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(32.dp))
            val labelWidth = 80.dp
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "username: ",
                    color = Color.Black,
                    modifier = Modifier.width(labelWidth)
                )
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("username       ") },
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
                print(username)


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
            Text(text = "Register", fontSize = 24.sp)
        }
    }
}
data class DropDownItem(
    val text: String
)
@Composable
fun Addpointonmap(navController: NavController) {
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
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current




            val labelWidth = 100.dp
            Spacer(modifier = Modifier.height(50.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {

                Text(
                    text = "Discription: ",
                    color = Color.Black,
                    modifier = Modifier.width(labelWidth)
                )
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

@Composable
fun nav(){
    val navController= rememberNavController()
    NavHost(navController = navController, startDestination ="map"  ){
        composable(route ="addpoint"){
            Addpointonmap(navController)
        }
        composable(route ="map"){
            Map(modifier = Modifier,navController)

        }
    }

}





    



