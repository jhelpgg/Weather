package fr.jhelp.weatherservice.model.shared

import java.util.*

data class WeatherCurrent(val time: Calendar,
                          val sunriseTime: Calendar,
                          val sunsetTime: Calendar,
                          val temperature : Temperature,
                          val temperatureFeels: Temperature,
                          val atmosphericPressureHectopascal: Double,
                          val humidityPercent: Int,
                          val weather: Weather)
