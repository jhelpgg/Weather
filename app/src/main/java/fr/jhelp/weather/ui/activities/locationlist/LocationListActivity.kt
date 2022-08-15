package fr.jhelp.weather.ui.activities.locationlist

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.jhelp.weather.R
import fr.jhelp.weather.model.LocationListModel
import fr.jhelp.weather.navigation.Navigation
import fr.jhelp.weather.ui.activities.locationlist.adapter.LocationListAdapter
import fr.jhelp.weatherservice.provider.provided
import fr.jhelp.weatherservice.startWeatherService
import fr.jhelp.weatherservice.stopWeatherService

class LocationListActivity : AppCompatActivity()
{
    private val locationListModel by provided<LocationListModel>()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        // Start the weather service
        startWeatherService(this)

        this.setContentView(R.layout.location_list_activity)

        val locationList = this.findViewById<RecyclerView>(R.id.locationList)
        locationList.adapter = LocationListAdapter(this)
        locationList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        this.findViewById<Button>(R.id.addLocationButton)
            .setOnClickListener { Navigation.showAddLocation() }

        this.locationListModel.refresh()
    }

    override fun onResume()
    {
        super.onResume()
        this.locationListModel.refresh()
    }

    override fun onDestroy()
    {
        // Stop the weather service
        stopWeatherService()

        super.onDestroy()
    }
}
