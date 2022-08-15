package fr.jhelp.weatherservice.model.shared

import fr.jhelp.weatherservice.tools.locationToDatabaseId

data class Location(val latitude: Double, val longitude: Double) : Comparable<Location> {
    override operator fun compareTo(other: Location): Int =
            locationToDatabaseId(this.latitude, this.longitude) - locationToDatabaseId(other.latitude, other.longitude)
}
