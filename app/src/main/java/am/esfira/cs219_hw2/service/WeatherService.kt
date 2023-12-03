package am.esfira.cs219_hw2.service

import am.esfira.cs219_hw2.model.CurrentResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("current.json")
    suspend fun getCurrent(@Query("q") q: String, @Query("key") key: String): CurrentResponse
}