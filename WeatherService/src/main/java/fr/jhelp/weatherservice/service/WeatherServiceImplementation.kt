package fr.jhelp.weatherservice.service

import fr.jhelp.weatherservice.database.WeatherDatabase
import fr.jhelp.weatherservice.model.shared.Location
import fr.jhelp.weatherservice.tools.tasks.promise.FutureResult

internal object WeatherServiceImplementation : WeatherServiceInterface {
    override fun requestWeatherLocation(latitude: Double, longitude: Double): WeatherLocationServiceInterface =
            WeatherLocationService(latitude, longitude)

    override fun knownLocations(): FutureResult<List<Location>> =
            WeatherDatabase.knownLocations()
}