package fr.jhelp.weather.model

import androidx.lifecycle.ViewModel
import fr.jhelp.weatherservice.model.shared.Location
import fr.jhelp.weatherservice.model.shared.WeatherDaily
import fr.jhelp.weatherservice.service.WeatherLocationServiceInterface

class NavigationModel : ViewModel()
{
    var location = Location(0.0, 0.0)
    lateinit var weatherLocationService: WeatherLocationServiceInterface
    var weatherDailyList : List<WeatherDaily> = emptyList()
    lateinit var weatherDaily: WeatherDaily
}
