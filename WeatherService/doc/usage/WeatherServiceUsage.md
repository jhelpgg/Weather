# Weather service usage

To be able use the **Weather Service** you have first to start it.

It is highly recommend to stop the service when it is no more need. By example just before exit
application.

````kotlin
// ...
import fr.jhelp.weatherservice.startWeatherService
import fr.jhelp.weatherservice.stopWeatherService

// ...

override fun onCreate(savedInstanceState: Bundle?)
{
    super.onCreate(savedInstanceState)
    // Start the weather service
    startWeatherService(this)
    // ...
}

// ...

override fun onDestroy()
{
    // Stop the weather service
    stopWeatherService()
    //...
    super.onDestroy()
}
````

Once the service it is started, it can be used from any where. To get the provided service :

````kotlin
// ...
import fr.jhelp.weatherservice.provider.provided
import fr.jhelp.weatherservice.service.WeatherServiceInterface

// ...

private val weatherService by provided<WeatherServiceInterface>()
````

The `provided` instruction retrieve a provided abject. Here the `WeatherServiceInterface`. The real
instance is resolve on first usage (like `lazy`). You hve to be sure `startWeatherService` is called
before the first usage of the field.

Now we have a weather service instance we can use it.

Weather service have tow methods :

`knownLocations` => List of current known locations (Locations already stored in database)

`requestWeatherLocation` => Obtain weather location service information

## knownLocations

It returns a future of the know location list.

The future can be link to do something as soon as the list is resolved.

By example link to a live data :

````kotlin
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.jhelp.weatherservice.model.shared.Location
import fr.jhelp.weatherservice.provider.provided
import fr.jhelp.weatherservice.service.WeatherServiceInterface
import fr.jhelp.weatherservice.tools.tasks.TaskContext

class LocationListModel : ViewModel()
{
    private val weatherService by provided<WeatherServiceInterface>()
    private val locationListMutable = MutableLiveData<List<Location>>(emptyList())
    val locationList = this.locationListMutable as LiveData<List<Location>>

    fun refresh()
    {
        this.weatherService.knownLocations().and(TaskContext.UI) { locations ->
            this.locationListMutable.value = locations
        }
    }
}
````

Tha ànd`function do a task when future succeed. We chose to play the task in UI thread since post
value in live data must be in UI thread.

## requestWeatherLocation

It returns a `WeatherLocationServiceInterface`. This object is necessary to track the request status
and get a `WeatherLocation`

`latitude` and `longitude` are the requested weather's location.

`futureLastKnownLocation` gives the current stored weather information.

`futureUpdatedLocation` give the last refreshed weather information.

They work like this

### Case 1

If their no information stored in database (first time request for the location) :

* Wait internet connection
* Download information
* Fill database
* `futureLastKnownLocation` and `futureUpdatedLocation` will be filled with the get information

### Case 2

If their information in database and the stored information is for the same day as the request

* Get database information
* `futureLastKnownLocation` and `futureUpdatedLocation` will be filled with the get information

### Case 3

If their information in database and the stored information and the request day is after stored
information

* Get database information
* `futureLastKnownLocation` filled with stored information
* Wait internet connection
* Download information
* Update database
* `futureUpdatedLocation` filled with the get information


