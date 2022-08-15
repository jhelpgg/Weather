package fr.jhelp.weatherservice.network

import android.util.Log
import fr.jhelp.weatherservice.extensions.parallel
import fr.jhelp.weatherservice.model.retrofit.RetrofitWeatherLocation
import fr.jhelp.weatherservice.model.retrofit.RetrofitWeatherRequest
import fr.jhelp.weatherservice.tools.TAG
import fr.jhelp.weatherservice.tools.tasks.TaskContext
import fr.jhelp.weatherservice.tools.tasks.promise.FutureResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal fun requestWeather(latitude: Double, longitude: Double): FutureResult<RetrofitWeatherLocation> =
        {
            try {
                val retrofit = Retrofit.Builder()
                        .baseUrl("https://api.openweathermap.org")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                val weatherRequest = retrofit.create(RetrofitWeatherRequest::class.java)
                val result = weatherRequest.requestWeather(latitude, longitude).execute()

                if (result.isSuccessful) {
                    result.body()!!
                } else {
                    Log.e(TAG, "${result.message()} \n ${result.errorBody()?.string()}")
                    throw RequestException("${result.message()} \n ${result.errorBody()?.string()}", latitude, longitude)
                }
            } catch (exception: Exception) {
                throw RequestException("Request issue !", latitude, longitude, exception)
            }
        }.parallel(TaskContext.NETWORK)
