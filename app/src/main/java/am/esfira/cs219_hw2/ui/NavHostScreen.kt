package am.esfira.cs219_hw2.ui

import am.esfira.cs219_hw2.R
import am.esfira.cs219_hw2.ui.theme.CS219_HW2Theme
import am.esfira.cs219_hw2.viewmodel.WeatherViewModel
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NavHostScreen(viewModel: WeatherViewModel, context: Context) {
    CS219_HW2Theme {

        var gpsPermissionState = rememberPermissionState(Manifest.permission.ACCESS_COARSE_LOCATION)
        var lat by remember { mutableDoubleStateOf(0.0) }
        var lng by remember { mutableDoubleStateOf(0.0) }

        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(key1 = lifecycleOwner, effect = {
            val eventObserver = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        gpsPermissionState.launchPermissionRequest()
                    }
                    else -> { /* Do nothing */ }
                }
            }
            lifecycleOwner.lifecycle.addObserver(eventObserver)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(eventObserver)
            }
        })

        val weatherData = viewModel.responses.value
        if (weatherData == null) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Text(
                    text = "Please wait while weather data is loading...",
                    modifier = Modifier.padding(20.dp)
                )
            }
        } else {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "WelcomeScreen") {
                    composable(
                        route = "WelcomeScreen",
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Welcome to Our App",
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Button(
                                onClick = { navController.navigate(route = "ListScreen") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                modifier = Modifier
                                    .width(256.dp)
                                    .height(48.dp)

                            ) {
                                Text(
                                    text = "Next",
                                    color = Color.White
                                )
                            }
                        }
                    }

                    composable(
                        route = "ListScreen",
                    ) {
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start
                        ) {
                            SettingsView()

                            if (gpsPermissionState.status.isGranted) {

                                getCurrentLocation(LocalContext.current) { latParam, lngParam ->
                                    lat = latParam
                                    lng = lngParam

                                    viewModel.updateCoordinates(latParam, lngParam)
                                }

                                if(viewModel.userLocationWeather.value != null) {
                                    Text(
                                        text = "User's coordinates - ${viewModel.userLocationWeather.value?.location?.name}",
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )

                                    // Show some attribute to the user, either of the following:
                                    // 1) Humidity in %
                                    // 2) Temperature in Celsius
                                    // 3) Temperature in Fahrenheit
                                    var attributeName: String = ""
                                    var attributeValue: String = ""
                                    var attributeUnit: String = ""

                                    val tempUnitPreference by context.temperatureUnitFlow.collectAsState(initial = "C")

                                    if (viewModel.userLocationWeather.value?.current?.temp_c != null
                                        && tempUnitPreference == "C"
                                    ) {
                                        attributeName = "Weather"
                                        attributeValue = viewModel.userLocationWeather.value?.current?.temp_c.toString()
                                        attributeUnit = "°C"
                                    } else if (viewModel.userLocationWeather.value?.current?.temp_f != null
                                        && tempUnitPreference == "F"
                                    ) {
                                        attributeName = "Weather"
                                        attributeValue = viewModel.userLocationWeather.value?.current?.temp_f.toString()
                                        attributeUnit = "°F"
                                    } else {
                                        attributeName = "Humidity"
                                        attributeValue = viewModel.userLocationWeather.value?.current?.humidity.toString()
                                        attributeUnit = "%"
                                    }
                                    Text(
                                        text = "${attributeName} - ${attributeValue}${attributeUnit}",
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                }
                            }

                            Text(
                                text = "List of cities",
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            CityView(
                                cityName = context.getString(R.string.yerevan_name),
                                cityDesc = context.getString(R.string.yerevan_desc),
                                cityImage = R.drawable.yerevan,
                                weatherResponse = weatherData[0]
                            )
                            CityView(
                                cityName = context.getString(R.string.rome_name),
                                cityDesc = context.getString(R.string.rome_desc),
                                cityImage = R.drawable.rome,
                                weatherResponse = weatherData[1]
                            )
                            CityView(
                                cityName = context.getString(R.string.bangkok_name),
                                cityDesc = context.getString(R.string.bangkok_name),
                                cityImage = R.drawable.bangkok,
                                weatherResponse = weatherData[2]
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun getCurrentLocation(context: Context, callback: (Double, Double) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val long = location.longitude
                callback(lat, long)
            }
        }
        .addOnFailureListener { exception ->
            // Handle location retrieval failure
            exception.printStackTrace()
        }
}