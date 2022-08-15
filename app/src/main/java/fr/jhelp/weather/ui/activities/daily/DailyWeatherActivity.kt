package fr.jhelp.weather.ui.activities.daily

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import fr.jhelp.weather.R
import fr.jhelp.weather.model.DailyWeatherModel
import fr.jhelp.weather.ui.activities.ActivityWithBackManaged
import fr.jhelp.weatherservice.provider.provided

class DailyWeatherActivity : ActivityWithBackManaged()
{
    private val dailyWeatherModel by provided<DailyWeatherModel>()
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.day_weather_activity)

        val location = this.dailyWeatherModel.location
        val weatherDaily = this.dailyWeatherModel.weatherDaily
        val weather = weatherDaily.weather
        val temperature = weatherDaily.temperatures.dayTemperature
        val temperatureFeels = weatherDaily.temperaturesFeels.dayTemperature

        this.findViewById<TextView>(R.id.currentWeatherLocationTitle)
            .text = this.getString(R.string.locationPattern,
                                   location.latitude,
                                   location.longitude)
        this.findViewById<ImageView>(R.id.weatherIcon)
            .setImageResource(weather.icon.imageResource)
        this.findViewById<TextView>(R.id.weatherName)
            .text = weather.name
        this.findViewById<TextView>(R.id.temperature)
            .text = this.getString(R.string.temperature, temperature.celsius)
        this.findViewById<TextView>(R.id.temperatureFeels)
            .text = this.getString(R.string.temperatureFeels, temperatureFeels.celsius)
        this.findViewById<TextView>(R.id.weatherDescription)
            .text = weather.description
    }
}