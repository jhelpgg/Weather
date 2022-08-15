package fr.jhelp.weather.model

import androidx.lifecycle.ViewModel
import fr.jhelp.weatherservice.provider.provided

class DailyWeatherModel : ViewModel()
{
    private val navigationModel by provided<NavigationModel>()
    val weatherDaily get() = this.navigationModel.weatherDaily
    val location get() = this.navigationModel.location
}