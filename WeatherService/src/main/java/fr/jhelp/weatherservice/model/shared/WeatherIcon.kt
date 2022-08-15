package fr.jhelp.weatherservice.model.shared

import androidx.annotation.DrawableRes
import fr.jhelp.weatherservice.R

enum class WeatherIcon(val reference: String, @DrawableRes val imageResource: Int) {
    WEATHER_01D("01d", R.drawable.weather_01d),
    WEATHER_01N("01n", R.drawable.weather_01n),
    WEATHER_02D("02d", R.drawable.weather_02d),
    WEATHER_02N("02n", R.drawable.weather_02n),
    WEATHER_03D("03d", R.drawable.weather_03d),
    WEATHER_03N("03n", R.drawable.weather_03n),
    WEATHER_04D("04d", R.drawable.weather_04d),
    WEATHER_04N("04n", R.drawable.weather_04n),
    WEATHER_09D("09d", R.drawable.weather_09d),
    WEATHER_09N("09n", R.drawable.weather_09n),
    WEATHER_10D("10d", R.drawable.weather_10d),
    WEATHER_10N("10n", R.drawable.weather_10n),
    WEATHER_11D("11d", R.drawable.weather_11d),
    WEATHER_11N("11n", R.drawable.weather_11n),
    WEATHER_13D("13d", R.drawable.weather_13d),
    WEATHER_13N("13n", R.drawable.weather_13n),
    WEATHER_50D("50d", R.drawable.weather_50d),
    WEATHER_50N("50n", R.drawable.weather_50n),
    UNKNOWN("", R.drawable.unknown)
    ;

    companion object {
        fun byReference(reference: String): WeatherIcon {
            for (weatherIcon in WeatherIcon.values()) {
                if (reference.equals(weatherIcon.reference, ignoreCase = true)) {
                    return weatherIcon
                }
            }

            return WeatherIcon.UNKNOWN
        }
    }
}
