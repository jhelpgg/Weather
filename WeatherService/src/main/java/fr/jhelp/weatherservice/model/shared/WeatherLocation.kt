package fr.jhelp.weatherservice.model.shared

data class WeatherLocation(val latitude: Double,
                           val longitude: Double,
                           val currentWeather: WeatherCurrent,
                           val dailyWeathers: List<WeatherDaily>)
