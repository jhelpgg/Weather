package fr.jhelp.weatherservice.model.retrofit

import com.google.gson.annotations.SerializedName

internal data class RetrofitTemperatureDay(@SerializedName("day") val dayTemperatureKelvin: Double,
                                           @SerializedName("night") val nightTemperatureKelvin: Double,
                                           @SerializedName("eve") val eveningTemperatureKelvin: Double,
                                           @SerializedName("morn") val morningTemperatureKelvin: Double)
