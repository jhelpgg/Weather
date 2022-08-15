package fr.jhelp.weatherservice.model.shared

data class Weather(val name: String,
                   val description: String,
                   val icon: WeatherIcon)
