package fr.jhelp.weather.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.jhelp.weather.navigation.Navigation
import fr.jhelp.weatherservice.model.shared.Location
import fr.jhelp.weatherservice.provider.provided
import fr.jhelp.weatherservice.service.WeatherServiceInterface
import fr.jhelp.weatherservice.tools.tasks.TaskContext

class LocationListModel : ViewModel()
{
    private val weatherService by provided<WeatherServiceInterface>()
    private val locationListMutable = MutableLiveData<List<Location>>(emptyList())
    val locationList = this.locationListMutable as LiveData<List<Location>>

    fun refresh()
    {
        this.weatherService.knownLocations().and(TaskContext.UI) { locations ->
            this.locationListMutable.value = locations
        }
    }

    fun currentLocationSelected(location: Location)
    {
        Navigation.showCurrent(location)
    }
}