package fr.jhelp.weatherservice.service

import fr.jhelp.weatherservice.database.WeatherDatabase
import fr.jhelp.weatherservice.extensions.observedBy
import fr.jhelp.weatherservice.extensions.sameDay
import fr.jhelp.weatherservice.model.retrofit.RetrofitWeatherLocation
import fr.jhelp.weatherservice.model.shared.WeatherLocation
import fr.jhelp.weatherservice.network.NetworkStatusCallback
import fr.jhelp.weatherservice.network.requestWeather
import fr.jhelp.weatherservice.tools.tasks.promise.FutureResult
import fr.jhelp.weatherservice.tools.tasks.promise.Promise
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class WeatherLocationService(override val latitude: Double,
                                      override val longitude: Double) :
        WeatherLocationServiceInterface
{
    private val promiseLastKnownLocation = Promise<WeatherLocation>()
    private val promiseUpdatedLocation = Promise<WeatherLocation>()
    private val statusMutableFlow =
        MutableStateFlow<WeatherLocationServiceStatus>(WeatherLocationServiceStatus.INITIALIZED)
    private lateinit var futureNetworkAvailable: FutureResult<Unit>
    override val futureLastKnownLocation = this.promiseLastKnownLocation.future
    override val futureUpdatedLocation = this.promiseUpdatedLocation.future
    override val statusFlow = this.statusMutableFlow.asStateFlow()

    init
    {
        val future = WeatherDatabase.obtainWeatherLocation(this.latitude, this.longitude)
        future.and { weatherLocation -> this.weatherLocationStored((weatherLocation)) }
        future.onError {
            this.statusMutableFlow.value = WeatherLocationServiceStatus.NO_VALUE
            this.requestUpdate()
        }
    }

    /**
     * Called if their location stored in database
     */
    private fun weatherLocationStored(weatherLocation: WeatherLocation)
    {
        this.promiseLastKnownLocation.result(weatherLocation)

        if (Calendar.getInstance().sameDay(weatherLocation.currentWeather.time))
        {
            this.promiseUpdatedLocation.result(weatherLocation)
            this.statusMutableFlow.value = WeatherLocationServiceStatus.FINISHED
            return
        }

        this.statusMutableFlow.value = WeatherLocationServiceStatus.NOT_UPDATED
        this.requestUpdate()
    }

    /**
     * Request weather to open weather API
     */
    private fun requestUpdate()
    {
        this.futureNetworkAvailable =
            NetworkStatusCallback.availableFlow.observedBy { available -> this.networkAvailable((available)) }
        val future = requestWeather(this.latitude, this.longitude)
        future.and { weatherLocation -> this.weatherLocationReceived(weatherLocation) }
        future.onError { error ->
            this.futureNetworkAvailable.cancel("Request failed!")
            this.promiseLastKnownLocation.error(error)
            this.promiseUpdatedLocation.error(error)
            this.statusMutableFlow.value = WeatherLocationServiceStatus.FINISHED
        }
    }

    /**
     * Call when open weather API respond
     */
    private fun weatherLocationReceived(retrofitWeatherLocation: RetrofitWeatherLocation)
    {
        this.futureNetworkAvailable.cancel("Request done!")
        val future = WeatherDatabase.store(retrofitWeatherLocation)
        future.andUnwrap { WeatherDatabase.obtainWeatherLocation(this.latitude, this.longitude) }
            .and { weatherLocation ->
                this.promiseLastKnownLocation.result(weatherLocation)
                this.promiseUpdatedLocation.result(weatherLocation)
                this.statusMutableFlow.value = WeatherLocationServiceStatus.FINISHED
            }
    }

    /**
     * React to network status change
     */
    private fun networkAvailable(available: Boolean)
    {
        if (this.statusMutableFlow.value == WeatherLocationServiceStatus.FINISHED)
        {
            return
        }

        if (available)
        {
            this.statusMutableFlow.value = WeatherLocationServiceStatus.DOWNLOADING
        }
        else
        {
            this.statusMutableFlow.value = WeatherLocationServiceStatus.WAIT_NETWORK
        }
    }
}