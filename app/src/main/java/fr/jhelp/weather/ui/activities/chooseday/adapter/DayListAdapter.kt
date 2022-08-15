package fr.jhelp.weather.ui.activities.chooseday.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.jhelp.weather.R
import fr.jhelp.weatherservice.model.shared.WeatherDaily

class DayListAdapter(private val weatherDailyList: List<WeatherDaily>) :
        RecyclerView.Adapter<DayViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder
    {
        val layoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dayIem = layoutInflater.inflate(R.layout.day_item, parent, false)
        return DayViewHolder(dayIem)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int)
    {
        holder.weatherDaily(this.weatherDailyList[position])
    }

    override fun getItemCount(): Int = this.weatherDailyList.size
}