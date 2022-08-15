package fr.jhelp.weatherservice.model.retrofit

import com.google.gson.annotations.SerializedName

internal data class RetrofitWeather(@SerializedName("main") val name: String,
                                    @SerializedName("description") val description: String,
                                    @SerializedName("icon") val icon: String)