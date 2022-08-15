package fr.jhelp.weatherservice.tools

import kotlin.math.round

/**
 * Convert a location to a database key.
 *
 * Trick to avoid store too near locations.
 *
 * If two position are enough near, they will be considered the same
 */
internal fun locationToDatabaseId(latitude: Double, longitude: Double): Int
{
    val latitudePart = round((latitude + 90.0) * 10.0).toInt()
    val longitudePart = round((longitude + 180.0) * 10.0).toInt()
    return (latitudePart shl 16) or longitudePart
}
