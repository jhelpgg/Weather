package fr.jhelp.weather.model

import androidx.lifecycle.ViewModel
import fr.jhelp.weather.navigation.Navigation
import fr.jhelp.weatherservice.model.shared.WeatherDaily
import fr.jhelp.weatherservice.provider.provided

class ChooseDayModel : ViewModel()
{
    val weatherDailyList by provided<List<WeatherDaily>>()
    fun chooseWeatherDaily(weatherDaily: WeatherDaily)
    {
        Navigation.showDaily(weatherDaily)
    }
}