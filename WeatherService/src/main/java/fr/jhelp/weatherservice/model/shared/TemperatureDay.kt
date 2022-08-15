package fr.jhelp.weatherservice.model.shared

data class TemperatureDay(val dayTemperature: Temperature,
                          val nightTemperature: Temperature,
                          val eveningTemperature: Temperature,
                          val morningTemperature: Temperature)
