package fr.jhelp.weather.model

import androidx.lifecycle.ViewModel
import fr.jhelp.weather.navigation.Navigation
import fr.jhelp.weatherservice.model.shared.WeatherDaily
import fr.jhelp.weatherservice.provider.provided

class ChooseDayModel : ViewModel()
{
    private val navigationModel by provided<NavigationModel>()
    val weatherDailyList get() = this.navigationModel.weatherDailyList
    fun chooseWeatherDaily(weatherDaily: WeatherDaily)
    {
        Navigation.showDaily(weatherDaily)
    }
}