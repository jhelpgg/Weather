package fr.jhelp.weather.ui.activities.current

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.MainThread
import fr.jhelp.weather.R
import fr.jhelp.weather.model.CurrentWeatherModel
import fr.jhelp.weather.tools.FormatDate
import fr.jhelp.weather.ui.activities.ActivityWithBackManaged
import fr.jhelp.weatherservice.model.shared.WeatherCurrent
import fr.jhelp.weatherservice.provider.provided

class CurrentWeatherActivity : ActivityWithBackManaged()
{
    private val currentWeatherModel by provided<CurrentWeatherModel>()
    private lateinit var dateOrLoadingText: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var weatherName: TextView
    private lateinit var temperature: TextView
    private lateinit var temperatureFeels: TextView
    private lateinit var weatherDescription: TextView
    private lateinit var chooseDayButton: Button

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.current_weather_activity)

        val currentWeatherLocationTitle =
            this.findViewById<TextView>(R.id.currentWeatherLocationTitle)
        this.dateOrLoadingText = this.findViewById(R.id.dateOrLoadingText)
        this.weatherIcon = this.findViewById(R.id.weatherIcon)
        this.weatherName = this.findViewById(R.id.weatherName)
        this.temperature = this.findViewById(R.id.temperature)
        this.temperatureFeels = this.findViewById(R.id.temperatureFeels)
        this.weatherDescription = this.findViewById(R.id.weatherDescription)
        this.chooseDayButton = this.findViewById(R.id.chooseDayButton)

        val location = this.currentWeatherModel.location
        currentWeatherLocationTitle.text = this.getString(R.string.locationPattern,
                                                          location.latitude,
                                                          location.longitude)

        this.currentWeatherModel.weatherCurrent.observe(this, this::showCurrentWeather)
        this.chooseDayButton.setOnClickListener { this.currentWeatherModel.chooseDay() }
    }

    @MainThread
    private fun showCurrentWeather(weatherCurrent: WeatherCurrent)
    {
        val weather = weatherCurrent.weather
        this.dateOrLoadingText.text = FormatDate.long(weatherCurrent.time)
        this.weatherIcon.setImageResource(weather.icon.imageResource)
        this.weatherName.text = weather.name
        this.temperature.text =
            this.getString(R.string.temperature, weatherCurrent.temperature.celsius)
        this.temperatureFeels.text =
            this.getString(R.string.temperatureFeels, weatherCurrent.temperatureFeels.celsius)
        this.weatherDescription.text = weather.description
        this.chooseDayButton.isEnabled = true
    }
}
