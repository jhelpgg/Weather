package fr.jhelp.weather.ui.activities

import androidx.appcompat.app.AppCompatActivity
import fr.jhelp.weather.navigation.Navigation

abstract class ActivityWithBackManaged : AppCompatActivity()
{
    override fun onBackPressed()
    {
        Navigation.back()
        this.finish()
    }
}
