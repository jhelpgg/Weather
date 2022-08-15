package fr.jhelp.weatherservice.network

class RequestException(val baseMessage: String, val latitude: Double, val longitude: Double,
                       cause: Throwable? = null) :
        Exception("Issue for get latitude=$latitude longitude=$$longitude \n $baseMessage", cause)