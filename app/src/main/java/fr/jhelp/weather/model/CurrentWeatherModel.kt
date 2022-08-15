package fr.jhelp.weather.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.jhelp.weather.navigation.Navigation
import fr.jhelp.weatherservice.model.shared.Location
import fr.jhelp.weatherservice.model.shared.WeatherCurrent
import fr.jhelp.weatherservice.model.shared.WeatherLocation
import fr.jhelp.weatherservice.provider.provided
import fr.jhelp.weatherservice.service.WeatherLocationServiceInterface
import fr.jhelp.weatherservice.tools.tasks.TaskContext
import java.util.concurrent.atomic.AtomicBoolean

class CurrentWeatherModel : ViewModel()
{
    private val weatherLocationService by provided<WeatherLocationServiceInterface>()
    private var weatherLocation: WeatherLocation? = null
    private val updatedWeatherLocation = AtomicBoolean(false)
    private val weatherCurrentMutable = MutableLiveData<WeatherCurrent>()
    val location =
        Location(this.weatherLocationService.latitude, this.weatherLocationService.longitude)
    val weatherCurrent = this.weatherCurrentMutable as LiveData<WeatherCurrent>

    init
    {
        this.weatherLocationService.futureLastKnownLocation
            .and { weatherLocation ->
                synchronized(this.updatedWeatherLocation) {
                    if (!this.updatedWeatherLocation.get())
                    {
                        this.weatherLocation = weatherLocation
                    }
                }

                weatherLocation.currentWeather
            }
            .and(TaskContext.UI) { weatherCurrent ->
                this.weatherCurrentMutable.value = weatherCurrent
            }

        this.weatherLocationService.futureUpdatedLocation
            .and { weatherLocation ->
                synchronized(this.updatedWeatherLocation) {
                    this.updatedWeatherLocation.set(true)
                    this.weatherLocation = weatherLocation
                }

                weatherLocation.currentWeather
            }
            .and(TaskContext.UI) { weatherCurrent ->
                this.weatherCurrentMutable.value = weatherCurrent
            }
    }

    fun chooseDay()
    {
        this.weatherLocation?.let { weatherLocation -> Navigation.showDailyList(weatherLocation) }
    }
}