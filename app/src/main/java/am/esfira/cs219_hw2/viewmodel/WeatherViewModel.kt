package am.esfira.cs219_hw2.viewmodel

import am.esfira.cs219_hw2.model.CurrentResponse
import am.esfira.cs219_hw2.service.RetrofitClient
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val API_KEY = "66dbabef80414e2fb3974610231511"

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
                val result = async { weatherService.getCurrent("$lat,$lng", API_KEY) }
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
                    async { weatherService.getCurrent(city, API_KEY) }
                }.awaitAll()

                _responses.value = results

            } catch (e: Exception) {
                Log.e("Retrofit", "Error fetching weather data", e)
            }
        }
    }
}
