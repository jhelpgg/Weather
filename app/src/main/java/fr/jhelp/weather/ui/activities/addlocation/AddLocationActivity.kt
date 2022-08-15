package fr.jhelp.weather.ui.activities.addlocation

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import fr.jhelp.weather.R
import fr.jhelp.weather.model.AddLocationModel
import fr.jhelp.weather.ui.activities.ActivityWithBackManaged
import fr.jhelp.weatherservice.model.shared.Location
import fr.jhelp.weatherservice.provider.provided

class AddLocationActivity : ActivityWithBackManaged()
{
    private val addLocationModel by provided<AddLocationModel>()
    private lateinit var latitudeEditText: EditText
    private lateinit var longitudeEditText: EditText
    private lateinit var addLocationButton: Button

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.add_location_activity)

        this.latitudeEditText = this.findViewById(R.id.latitudeEditText)
        this.longitudeEditText = this.findViewById(R.id.longitudeEditText)
        this.addLocationButton = this.findViewById(R.id.addLocationButton)

        this.latitudeEditText.doOnTextChanged { _, _, _, _ ->
            this.addLocationButton.isEnabled = this.parseLocation() != null
        }
        this.longitudeEditText.doOnTextChanged { _, _, _, _ ->
            this.addLocationButton.isEnabled = this.parseLocation() != null
        }

        this.addLocationButton.setOnClickListener {
            this.parseLocation()
                ?.let { location -> this.addLocationModel.currentLocationSelected(location) }
        }
    }

    private fun parseLocation(): Location?
    {
        val latitude = this.latitudeEditText.text.toString().toDoubleOrNull() ?: return null

        if (latitude < -90.0 || latitude > 90.00)
        {
            return null
        }

        val longitude = this.longitudeEditText.text.toString().toDoubleOrNull() ?: return null

        if (longitude < -180.0 || longitude > 180.00)
        {
            return null
        }

        return Location(latitude, longitude)
    }

}