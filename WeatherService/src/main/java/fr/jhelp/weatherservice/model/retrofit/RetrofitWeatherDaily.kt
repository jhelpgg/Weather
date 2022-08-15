package fr.jhelp.weatherservice.model.retrofit

import com.google.gson.annotations.SerializedName

internal data class RetrofitWeatherDaily(@SerializedName("dt") val timeUTC: Long,
                                         @SerializedName("sunrise") val sunriseTimeUTC: Long,
                                         @SerializedName("sunset") val sunsetTimeUTC: Long,
                                         @SerializedName("temp") val temperatures: RetrofitTemperatureDay,
                                         @SerializedName("feels_like") val temperaturesFeels: RetrofitTemperatureDay,
                                         @SerializedName("pressure") val atmosphericPressureHectopascal: Double,
                                         @SerializedName("humidity") val humidityPercent: Int,
                                         @SerializedName("weather") val weather: List<RetrofitWeather>)