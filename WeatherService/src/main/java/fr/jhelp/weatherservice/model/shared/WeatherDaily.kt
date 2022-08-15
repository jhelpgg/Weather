package fr.jhelp.weatherservice.model.shared

import java.util.*

data class WeatherDaily(val time: Calendar,
                        val sunriseTime: Calendar,
                        val sunsetTime: Calendar,
                        val temperatures: TemperatureDay,
                        val temperaturesFeels: TemperatureDay,
                        val atmosphericPressureHectopascal: Double,
                        val humidityPercent: Int,
                        val weather: Weather)
