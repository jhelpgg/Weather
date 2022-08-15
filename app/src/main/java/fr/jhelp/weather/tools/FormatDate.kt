package fr.jhelp.weather.tools

import android.content.Context
import android.text.format.DateFormat
import fr.jhelp.weatherservice.provider.provided
import java.util.*

object FormatDate {
    private val context: Context by provided<Context>()

    fun standard(date: Calendar): String = DateFormat.getDateFormat(this.context).format(date.time)
    fun medium(date: Calendar): String = DateFormat.getMediumDateFormat(this.context).format(date.time)
    fun long(date: Calendar): String = DateFormat.getLongDateFormat(this.context).format(date.time)
}