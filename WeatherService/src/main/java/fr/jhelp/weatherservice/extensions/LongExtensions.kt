package fr.jhelp.weatherservice.extensions

import java.util.*


val Long.utc: Calendar
    get() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this * 1000L
        return calendar
    }