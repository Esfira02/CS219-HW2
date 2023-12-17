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
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "WelcomeScreen") {
                    composable(
                        route = "WelcomeScreen",
                    ) {
                        WelcomeScreenView(navController)
                    }

                    composable(
                        route = "ListScreen",
                    ) {
                        ListScreenView(viewModel, context, gpsPermissionState, weatherData)
                    }
                }
            }
        }
    }
}