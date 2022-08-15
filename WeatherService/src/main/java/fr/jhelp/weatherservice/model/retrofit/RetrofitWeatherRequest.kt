package fr.jhelp.weatherservice.model.retrofit

import fr.jhelp.weatherservice.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

internal interface RetrofitWeatherRequest {
    @GET("/data/3.0/onecall?")
    fun requestWeather(@Query("lat") latitude: Double,
                       @Query("lon") longitude: Double,
                       @Query("exclude") excludes: String = "minutely,hourly,alerts",
                       @Query("appid") apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY,
                       @Query("lang") language: String = Locale.getDefault().language)
            : Call<RetrofitWeatherLocation>
}