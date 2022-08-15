package fr.jhelp.weather.model

import androidx.lifecycle.ViewModel
import fr.jhelp.weather.navigation.Navigation
import fr.jhelp.weatherservice.model.shared.Location

class AddLocationModel : ViewModel()
{
    fun currentLocationSelected(location: Location)
    {
        Navigation.showCurrent(location)
    }
}