package fr.jhelp.weatherservice.database

// **********************
// *** Common columns ***
// **********************
internal const val COLUMN_ID = "ID"

// **********************
// *** TemperatureDay ***
// **********************
internal const val TABLE_TEMPERATURE_DAY = "TemperatureDay"
internal const val COLUMN_DAY_TEMPERATURE = "dayTemperature"
internal const val COLUMN_NIGHT_TEMPERATURE = "nightTemperature"
internal const val COLUMN_EVENING_TEMPERATURE = "eveningTemperature"
internal const val COLUMN_MORNING_TEMPERATURE = "morningTemperature"

// ***************
// *** Weather ***
// ***************
internal const val TABLE_WEATHER = "Weather"
internal const val COLUMN_NAME = "name"
internal const val COLUMN_DESCRIPTION = "description"
internal const val COLUMN_ICON = "icon"

// ************************************************
// *** Common columns between current and daily ***
// ************************************************
internal const val COLUMN_TIME = "time"
internal const val COLUMN_SUNRISE_TIME = "sunriseTime"
internal const val COLUMN_SUNSET_TIME = "sunsetTime"
internal const val COLUMN_ATMOSPHERIC_PRESSURE = "atmosphericPressure"
internal const val COLUMN_HUMIDITY_PERCENT= "humidityPercent"
internal const val COLUMN_WEATHER_ID = "weatherID"

// **********************
// *** WeatherCurrent ***
// **********************
internal const val TABLE_WEATHER_CURRENT="WeatherCurrent"
internal const val COLUMN_TEMPERATURE = "temperature"
internal const val COLUMN_TEMPERATURE_FEELS = "temperatureFeels"

// ********************
// *** WeatherDaily ***
// ********************
internal const val TABLE_WEATHER_DAILY="WeatherDaily"
internal const val COLUMN_TEMPERATURE_ID = "temperaturesID"
internal const val COLUMN_TEMPERATURE_FEELS_ID = "temperaturesFeelsID"
internal const val COLUMN_WEATHER_LOCATION_ID = "weatherLocationID"

// ***********************
// *** WeatherLocation ***
// ***********************
internal const val TABLE_WEATHER_LOCATION="WeatherLocation"
internal const val COLUMN_LATITUDE = "latitude"
internal const val COLUMN_LONGITUDE = "longitude"
internal const val COLUMN_CURRENT_WEATHER_ID = "currentWeatherID"
