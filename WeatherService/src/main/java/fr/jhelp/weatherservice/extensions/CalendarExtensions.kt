package fr.jhelp.weatherservice.extensions

import java.util.*

fun Calendar.sameDay(calendar: Calendar): Boolean =
        this[Calendar.DAY_OF_MONTH] == calendar[Calendar.DAY_OF_MONTH]
                && this[Calendar.MONTH] == calendar[Calendar.MONTH]
                && this[Calendar.YEAR] == calendar[Calendar.YEAR]
