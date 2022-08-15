package fr.jhelp.weatherservice.service

enum class WeatherLocationServiceStatus {
    INITIALIZED,
    NO_VALUE,
    NOT_UPDATED,
    WAIT_NETWORK,
    DOWNLOADING,
    FINISHED
}