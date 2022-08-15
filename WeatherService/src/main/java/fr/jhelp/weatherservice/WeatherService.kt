package fr.jhelp.weatherservice

import android.content.Context
import fr.jhelp.weatherservice.database.WeatherDatabase
import fr.jhelp.weatherservice.network.NetworkStatusManager
import fr.jhelp.weatherservice.provider.forget
import fr.jhelp.weatherservice.provider.isProvided
import fr.jhelp.weatherservice.provider.provideSingle
import fr.jhelp.weatherservice.service.WeatherServiceImplementation
import fr.jhelp.weatherservice.service.WeatherServiceInterface

/**
 * Launch the weather service
 *
 * Call it before use the weather service
 *
 * It provides implementation for [WeatherServiceInterface]
 */
fun startWeatherService(context: Context)
{
    val applicationContext = context.applicationContext
    provideSingle<Context> { applicationContext }
    NetworkStatusManager.initialize()
    provideSingle<WeatherServiceInterface> { WeatherServiceImplementation }
}

/**
 * Stop properly the weather service
 *
 * Call it to avoid potential memory leaks
 */
fun stopWeatherService()
{
    if (isProvided<Context>())
    {
        NetworkStatusManager.destroy()
        WeatherDatabase.close()
        forget<WeatherServiceInterface>()
        forget<Context>()
    }
}
