package fr.jhelp.weatherservice.service

import fr.jhelp.weatherservice.model.shared.Location
import fr.jhelp.weatherservice.tools.tasks.promise.FutureResult

/**
 * Link to the weather service
 */
interface WeatherServiceInterface {
    /**
     * Request weather for requested location
     *
     * @return Object to track to response
     */
    fun requestWeatherLocation(latitude:Double,longitude:Double) : WeatherLocationServiceInterface

    /**
     * List of locations stored in database
     */
    fun knownLocations(): FutureResult<List<Location>>
}