package fr.jhelp.weatherservice.service

import fr.jhelp.weatherservice.model.shared.WeatherLocation
import fr.jhelp.weatherservice.tools.tasks.promise.FutureResult
import kotlinx.coroutines.flow.Flow

/**
 * Track weather request response
 */
interface WeatherLocationServiceInterface {
    val latitude : Double
    val longitude : Double
    /** Complete when last known location is resolved */
    val futureLastKnownLocation:FutureResult<WeatherLocation>
    /** Complete when a fresh location is received */
    val futureUpdatedLocation:FutureResult<WeatherLocation>
    /** To follow the request progression status */
    val statusFlow : Flow<WeatherLocationServiceStatus>
}