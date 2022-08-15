package fr.jhelp.weather.ui.activities.locationlist.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.jhelp.weather.R
import fr.jhelp.weather.model.LocationListModel
import fr.jhelp.weatherservice.model.shared.Location
import fr.jhelp.weatherservice.provider.provided

class LocationViewHolder(view: View) : RecyclerView.ViewHolder(view)
{
    private val locationListModel by provided<LocationListModel>()
    private val locationItem = this.itemView.findViewById<TextView>(R.id.locationItem)

    fun location(location: Location)
    {
        this.locationItem.text = this.itemView.resources.getString(R.string.locationPattern,
                                                                   location.latitude,
                                                                   location.longitude)
        this.locationItem.setOnClickListener {
            this.locationListModel.currentLocationSelected(location)
        }
    }
}