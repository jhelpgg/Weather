package fr.jhelp.weather.ui.activities.locationlist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import fr.jhelp.weather.R
import fr.jhelp.weather.model.LocationListModel
import fr.jhelp.weatherservice.model.shared.Location
import fr.jhelp.weatherservice.provider.provided

class LocationListAdapter(lifecycleOwner: LifecycleOwner) :
        RecyclerView.Adapter<LocationViewHolder>()
{
    private var locationList: List<Location> = emptyList()
    private val locationListModel by provided<LocationListModel>()

    init
    {
        this.locationListModel.locationList.observe(lifecycleOwner) { list ->
            this.locationList = list
            this.notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder
    {
        val layoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val locationItem = layoutInflater.inflate(R.layout.location_item, parent, false)
        return LocationViewHolder(locationItem)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int)
    {
        holder.location(this.locationList[position])
    }

    override fun getItemCount(): Int = this.locationList.size
}