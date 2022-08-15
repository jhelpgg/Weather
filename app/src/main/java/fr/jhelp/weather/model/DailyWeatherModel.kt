package fr.jhelp.weather.model

import androidx.lifecycle.ViewModel
import fr.jhelp.weatherservice.model.shared.Location
import fr.jhelp.weatherservice.model.shared.WeatherDaily
import fr.jhelp.weatherservice.provider.provided

class DailyWeatherModel : ViewModel()
{
    val weatherDaily by provided<WeatherDaily>()
    val location by provided<Location>()
}