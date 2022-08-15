package fr.jhelp.weather

import android.app.Application
import fr.jhelp.weather.model.AddLocationModel
import fr.jhelp.weather.model.ChooseDayModel
import fr.jhelp.weather.model.CurrentWeatherModel
import fr.jhelp.weather.model.DailyWeatherModel
import fr.jhelp.weather.model.LocationListModel
import fr.jhelp.weather.model.NavigationModel
import fr.jhelp.weatherservice.provider.provideSingle

class WeatherApplication : Application()
{
    override fun onCreate()
    {
        super.onCreate()
        // Provide the models
        provideSingle { NavigationModel() }
        provideSingle { LocationListModel() }
        provideSingle { AddLocationModel() }
        provideSingle { CurrentWeatherModel() }
        provideSingle { ChooseDayModel() }
        provideSingle { DailyWeatherModel() }
    }
}