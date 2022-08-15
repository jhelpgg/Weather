package fr.jhelp.weather.ui.activities.chooseday

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.jhelp.weather.R
import fr.jhelp.weather.model.ChooseDayModel
import fr.jhelp.weather.ui.activities.ActivityWithBackManaged
import fr.jhelp.weather.ui.activities.chooseday.adapter.DayListAdapter
import fr.jhelp.weatherservice.provider.provided

class ChooseDayActivity : ActivityWithBackManaged()
{
    private val chooseDayModel by provided<ChooseDayModel>()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.choose_day_activity)
        val dayList = this.findViewById<RecyclerView>(R.id.dayList)
        dayList.adapter = DayListAdapter(this.chooseDayModel.weatherDailyList)
        dayList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }
}