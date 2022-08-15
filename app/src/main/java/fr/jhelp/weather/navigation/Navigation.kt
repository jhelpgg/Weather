package fr.jhelp.weather.navigation

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import fr.jhelp.weather.ui.activities.addlocation.AddLocationActivity
import fr.jhelp.weather.ui.activities.chooseday.ChooseDayActivity
import fr.jhelp.weather.ui.activities.current.CurrentWeatherActivity
import fr.jhelp.weather.ui.activities.daily.DailyWeatherActivity
import fr.jhelp.weatherservice.model.shared.Location
import fr.jhelp.weatherservice.model.shared.WeatherDaily
import fr.jhelp.weatherservice.model.shared.WeatherLocation
import fr.jhelp.weatherservice.provider.provideSingle
import fr.jhelp.weatherservice.provider.provided
import fr.jhelp.weatherservice.service.WeatherServiceInterface
import fr.jhelp.weatherservice.tools.tasks.promise.FutureResult

object Navigation
{
    private val context: Context by provided<Context>()
    private val weatherService: WeatherServiceInterface by provided<WeatherServiceInterface>()
    private var currentState = NavigationState.LocationListState

    fun showAddLocation()
    {
        if (this.currentState == NavigationState.LocationListState)
        {
            this.currentState = NavigationState.AddLocationState
            val intent = Intent(this.context, AddLocationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY
            this.context.startActivity(intent)
        }
    }

    fun showCurrent(location: Location)
    {
        if (this.currentState == NavigationState.LocationListState
            || this.currentState == NavigationState.AddLocationState)
        {
            this.currentState = NavigationState.ShowCurrentWeather
            val weatherLocationServiceInterface =
                this.weatherService.requestWeatherLocation(location.latitude, location.longitude)
            provideSingle { location }
            provideSingle { weatherLocationServiceInterface }
            val intent = Intent(this.context, CurrentWeatherActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY
            this.context.startActivity(intent)
        }
    }

    fun showDailyList(weatherLocation: WeatherLocation)
    {
        if (this.currentState == NavigationState.ShowCurrentWeather)
        {
            this.currentState = NavigationState.ChooseDay
            provideSingle { weatherLocation.dailyWeathers }
            val intent = Intent(this.context, ChooseDayActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY
            this.context.startActivity(intent)
        }
    }

    fun showDaily(weatherDaily: WeatherDaily)
    {
        if (this.currentState == NavigationState.ChooseDay)
        {
            this.currentState = NavigationState.ShowDailyWeather
            provideSingle { weatherDaily }
            val intent = Intent(this.context, DailyWeatherActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY
            this.context.startActivity(intent)
        }
    }

    fun back()
    {
        var activity: Class<out ComponentActivity>? = null

        when (this.currentState)
        {
            NavigationState.LocationListState  -> Unit
            NavigationState.AddLocationState   ->
                this.currentState = NavigationState.LocationListState
            NavigationState.ShowCurrentWeather ->
                this.currentState = NavigationState.LocationListState
            NavigationState.ChooseDay          ->
            {
                this.currentState = NavigationState.ShowCurrentWeather
                activity = CurrentWeatherActivity::class.java
            }
            NavigationState.ShowDailyWeather   ->
            {
                this.currentState = NavigationState.ChooseDay
                activity = ChooseDayActivity::class.java
            }
        }

        activity?.let { classActivity ->
            val intent = Intent(this.context, classActivity)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY
            this.context.startActivity(intent)
        }
    }

    fun knownLocations(): FutureResult<List<Location>> = this.weatherService.knownLocations()
}
