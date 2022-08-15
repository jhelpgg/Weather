package fr.jhelp.weatherservice.model.retrofit

import com.google.gson.annotations.SerializedName

internal data class RetrofitWeatherLocation(@SerializedName("lat") val latitude: Double,
                                            @SerializedName("lon") val longitude: Double,
                                            @SerializedName("current") val currentWeather: RetrofitWeatherCurrent,
                                            @SerializedName("daily") val dailyWeathers: List<RetrofitWeatherDaily>)
