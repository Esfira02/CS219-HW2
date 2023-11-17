@file:OptIn(ExperimentalPermissionsApi::class)

package am.esfira.cs219_hw2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import am.esfira.cs219_hw2.ui.theme.CS219_HW2Theme
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavHostScreen(viewModel())
        }
    }
}

val descriptions = mapOf("Yerevan" to "Yerevan is the capital city of Armenia. It is one of the oldest cities in the world, having a beautiful heritage.",
    "Bangkok" to "Bangkok, Thailand’s capital, is a large city known for ornate shrines and vibrant street life.",
    "Rome" to "Rome is the capital city of Italy. It is also the capital of the Lazio region, the centre of the Metropolitan City of Rome, and a special comune named Comune di Roma Capitale.")

object RetrofitClient {
    private const val BASE_URL = "https://api.weatherapi.com/v1/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val weatherService: WeatherService by lazy {
        retrofit.create(WeatherService::class.java)
    }
}

class WeatherViewModel : ViewModel() {
    private val weatherService = RetrofitClient.weatherService

    private val _responses = MutableLiveData<List<CurrentResponse>>()
    val responses: LiveData<List<CurrentResponse>> get() = _responses

    private val _lat = MutableLiveData<Double>()
    private val _lng = MutableLiveData<Double>()

    val lat: LiveData<Double> get() = _lat
    val lng: LiveData<Double> get() = _lng

    private val _userLocationWeather = MutableLiveData<CurrentResponse>()
    val userLocationWeather: LiveData<CurrentResponse> get() = _userLocationWeather

    fun updateCoordinates(latParam: Double, lngParam: Double) {
        _lat.value = latParam
        _lng.value = lngParam

        viewModelScope.launch {
            try {
                val result = async { weatherService.getCurrent("$lat,$lng") }
                _userLocationWeather.value = result.await()
            } catch (e: Exception) {
                Log.e("Retrofit", "Error fetching weather data", e)
            }
        }
    }

    init {
        viewModelScope.launch {
            try {
                val cities = listOf("Yerevan", "Rome", "Bangkok")

                val results = cities.map { city ->
                    async { weatherService.getCurrent(city) }
                }.awaitAll()

                _responses.value = results

            } catch (e: Exception) {
                Log.e("Retrofit", "Error fetching weather data", e)
            }
        }
    }
}

@Composable
fun CityView(
    cityName: String,
    cityDesc: String,
    cityImage: Int,
    weatherResponse: CurrentResponse
){
    Column(modifier = Modifier
        .padding(horizontal = 10.dp)
        .fillMaxWidth()){
        Text(
            text = cityName,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
        Text(
            text = cityDesc
        )
        Spacer(modifier = Modifier.height(15.dp))
        Image(
            painter = painterResource(id = cityImage),
            contentDescription = cityName,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = weatherResponse.current.tempC.toString()
                    + "°C - "
                    + weatherResponse.current.condition
        )
    }
}

@Composable
fun NavHostScreen(viewModel: WeatherViewModel) {
    CS219_HW2Theme {

        var gpsPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_COARSE_LOCATION)
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

                                    Text(
                                        text = "Weather - ${viewModel.userLocationWeather.value?.current}°C",
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                }

                            }

                            Text(
                                text = "List of cities",
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            CityView(
                                cityName = "Yerevan",
                                cityDesc = descriptions["Yerevan"].toString(),
                                cityImage = R.drawable.yerevan,
                                weatherResponse = weatherData[0]
                            )
                            CityView(
                                cityName = "Rome",
                                cityDesc = descriptions["Rome"].toString(),
                                cityImage = R.drawable.rome,
                                weatherResponse = weatherData[1]
                            )
                            CityView(
                                cityName = "Bangkok",
                                cityDesc = descriptions["Bangkok"].toString(),
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

interface WeatherService {
    @GET("current.json")
    suspend fun getCurrent(@Query("q") q: String): CurrentResponse
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