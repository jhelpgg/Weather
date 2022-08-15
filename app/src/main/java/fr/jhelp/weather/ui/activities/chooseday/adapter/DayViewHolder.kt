package fr.jhelp.weather.ui.activities.chooseday.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.jhelp.weather.R
import fr.jhelp.weather.model.ChooseDayModel
import fr.jhelp.weather.tools.FormatDate
import fr.jhelp.weatherservice.model.shared.WeatherDaily
import fr.jhelp.weatherservice.provider.provided

class DayViewHolder(view: View) : RecyclerView.ViewHolder(view)
{
    private val chooseDayModel by provided<ChooseDayModel>()
    private val weatherIcon = this.itemView.findViewById<ImageView>(R.id.weatherIcon)
    private val dayDate = this.itemView.findViewById<TextView>(R.id.dayDate)
    private val temperature = this.itemView.findViewById<TextView>(R.id.temperature)

    fun weatherDaily(weatherDaily: WeatherDaily)
    {
        this.weatherIcon.setImageResource(weatherDaily.weather.icon.imageResource)
        this.dayDate.text = FormatDate.medium(weatherDaily.time)
        this.temperature.text = this.itemView.resources.getString(R.string.temperature,
                                                                  weatherDaily.temperatures.dayTemperature.celsius)
        this.itemView.setOnClickListener { this.chooseDayModel.chooseWeatherDaily(weatherDaily) }
    }
}