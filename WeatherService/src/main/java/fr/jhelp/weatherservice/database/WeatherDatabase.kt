package fr.jhelp.weatherservice.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import fr.jhelp.weatherservice.extensions.parallel
import fr.jhelp.weatherservice.extensions.utc
import fr.jhelp.weatherservice.model.retrofit.*
import fr.jhelp.weatherservice.model.shared.*
import fr.jhelp.weatherservice.provider.provided
import fr.jhelp.weatherservice.tools.TAG
import fr.jhelp.weatherservice.tools.locationToDatabaseId
import fr.jhelp.weatherservice.tools.tasks.TaskContext
import fr.jhelp.weatherservice.tools.tasks.promise.FutureResult

internal object WeatherDatabase {
    private val context: Context by provided<Context>()
    private val weatherSQLiteOpenHelper: WeatherSQLiteOpenHelper = WeatherSQLiteOpenHelper(this.context)

    fun obtainWeatherLocation(latitude: Double, longitude: Double): FutureResult<WeatherLocation> =
            {
                val database = this.weatherSQLiteOpenHelper.readableDatabase
                val id = locationToDatabaseId(latitude, longitude)
                var found = false
                var latitudeRead = 0.0
                var longitudeRead = 0.0
                var currentWeatherID = -1L

                val cursor = database.query(
                        TABLE_WEATHER_LOCATION, // table
                        arrayOf(COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_CURRENT_WEATHER_ID), // columns
                        "$COLUMN_ID=?", //selection
                        arrayOf(id.toString()), // selectionArgs
                        null, //groupBy
                        null, //having
                        null, //orderBy
                        "1" //limit
                )

                if (cursor.moveToNext()) {
                    found = true
                    latitudeRead = cursor.getDouble(0)
                    longitudeRead = cursor.getDouble(1)
                    currentWeatherID = cursor.getLong(2)
                }

                cursor.close()

                if (found) {
                    val currentWeather = this.obtainWeatherCurrent(currentWeatherID, database)
                    val weatherDailyList = this.obtainWeatherDailyList(id, database)
                    database.close()
                    WeatherLocation(latitudeRead, longitudeRead, currentWeather, weatherDailyList)
                } else {
                    database.close()
                    throw IllegalStateException("No data for latitude=$latitude, longitude=$longitude")
                }
            }.parallel(TaskContext.IO)

    fun knownLocations(): FutureResult<List<Location>> =
            {
                val database = this.weatherSQLiteOpenHelper.readableDatabase
                val list = ArrayList<Location>()

                val cursor = database.query(
                        TABLE_WEATHER_LOCATION, // table
                        arrayOf(COLUMN_LATITUDE, COLUMN_LONGITUDE), // columns
                        null, //selection
                        null, // selectionArgs
                        null, //groupBy
                        null, //having
                        null //orderBy
                )

                while (cursor.moveToNext()) {
                    val latitude = cursor.getDouble(0)
                    val longitude = cursor.getDouble(1)
                    list.add(Location(latitude, longitude))
                }

                cursor.close()
                database.close()
                list.sort()
                list
            }.parallel(TaskContext.IO)

    fun close() {
        this.weatherSQLiteOpenHelper.close()
    }

    internal fun store(weatherLocation: RetrofitWeatherLocation): FutureResult<Unit> =
            {
                val database = this.weatherSQLiteOpenHelper.writableDatabase
                val id = locationToDatabaseId(weatherLocation.latitude, weatherLocation.longitude)

                database.beginTransaction()

                try {
                    if (this.weatherLocationExists(id, database)) {
                        this.updateWeatherLocation(id, weatherLocation, database)
                    } else {
                        this.insertWeatherLocation(id, weatherLocation, database)
                    }

                    database.setTransactionSuccessful()
                } catch (exception: Exception) {
                    Log.e(TAG, "Issue while store data", exception)
                } finally {
                    database.endTransaction()
                }

                database.close()
            }.parallel(TaskContext.IO)

    private fun weatherLocationExists(id: Int, database: SQLiteDatabase): Boolean =
            this.obtainID(database, TABLE_WEATHER_LOCATION, COLUMN_ID, id.toLong()) >= 0L

    private fun obtainID(database: SQLiteDatabase, table: String, idColumn: String, idSelectValue: Long): Long {
        val cursor = database.query(
                table, // table
                arrayOf(idColumn), // columns
                "$COLUMN_ID=?", //selection
                arrayOf(idSelectValue.toString()), // selectionArgs
                null, //groupBy
                null, //having
                null, //orderBy
                "1" //limit
        )

        val id =
                if (cursor.moveToNext()) {
                    cursor.getLong(0)
                } else {
                    -1L
                }

        cursor.close()
        return id
    }

    private fun insertWeatherLocation(id: Int, weatherLocation: RetrofitWeatherLocation, database: SQLiteDatabase) {
        val weatherCurrentId = this.insertWeatherCurrent(weatherLocation.currentWeather, database)
        val content = weatherLocationContent(id, weatherLocation, weatherCurrentId)
        database.insert(TABLE_WEATHER_LOCATION, null, content)

        for (weatherDaily in weatherLocation.dailyWeathers) {
            this.insertWeatherDaily(id, weatherDaily, database)
        }
    }

    private fun insertWeatherCurrent(weatherCurrent: RetrofitWeatherCurrent, database: SQLiteDatabase): Long {
        val weatherID = this.insertWeather(weatherCurrent.weather[0], database)
        val content = this.weatherCurrentContent(weatherCurrent, weatherID)
        return database.insert(TABLE_WEATHER_CURRENT, null, content)
    }

    private fun insertWeatherDaily(weatherLocationId: Int, weatherDaily: RetrofitWeatherDaily, database: SQLiteDatabase) {
        val weatherID = this.insertWeather(weatherDaily.weather[0], database)
        val temperaturesID = this.insertTemperatureDay(weatherDaily.temperatures, database)
        val temperaturesFeelsID = this.insertTemperatureDay(weatherDaily.temperaturesFeels, database)
        val content = this.weatherDailyContent(weatherDaily, weatherLocationId, weatherID, temperaturesID, temperaturesFeelsID)
        database.insert(TABLE_WEATHER_DAILY, null, content)
    }

    private fun insertWeather(weather: RetrofitWeather, database: SQLiteDatabase): Long {
        val content = weatherContent(weather)
        return database.insert(TABLE_WEATHER, null, content)
    }

    private fun insertTemperatureDay(temperatureDay: RetrofitTemperatureDay, database: SQLiteDatabase): Long {
        val content = temperatureDayContent(temperatureDay)
        return database.insert(TABLE_TEMPERATURE_DAY, null, content)
    }

    private fun updateWeatherLocation(id: Int, weatherLocation: RetrofitWeatherLocation, database: SQLiteDatabase) {
        val weatherCurrentID = this.obtainID(database, TABLE_WEATHER_LOCATION, COLUMN_CURRENT_WEATHER_ID, id.toLong())
        this.updateWeatherCurrent(weatherCurrentID, weatherLocation.currentWeather, database)
        val content = weatherLocationContent(id, weatherLocation)
        database.update(TABLE_WEATHER_LOCATION, content, "$COLUMN_ID=?", arrayOf(id.toString()))

        database.delete(TABLE_WEATHER_DAILY, "$COLUMN_WEATHER_LOCATION_ID=?", arrayOf(id.toString()))

        for (weatherDaily in weatherLocation.dailyWeathers) {
            this.insertWeatherDaily(id, weatherDaily, database)
        }
    }

    private fun updateWeatherCurrent(weatherCurrentID: Long, weatherCurrent: RetrofitWeatherCurrent, database: SQLiteDatabase) {
        val weatherID = this.obtainID(database, TABLE_WEATHER_CURRENT, COLUMN_WEATHER_ID, weatherCurrentID)
        this.updateWeather(weatherID, weatherCurrent.weather[0], database)
        val content = weatherCurrentContent(weatherCurrent)
        database.update(TABLE_WEATHER_CURRENT, content, "$COLUMN_ID=?", arrayOf(weatherCurrentID.toString()))
    }

    private fun updateWeather(weatherID: Long, weather: RetrofitWeather, database: SQLiteDatabase) {
        val content = weatherContent(weather)
        database.update(TABLE_WEATHER, content, "$COLUMN_ID=?", arrayOf(weatherID.toString()))
    }

    private fun weatherLocationContent(id: Int, weatherLocation: RetrofitWeatherLocation,
                                       weatherCurrentId: Long = -1L): ContentValues {
        val contentValues = ContentValues()
        contentValues.put(COLUMN_ID, id)
        contentValues.put(COLUMN_LATITUDE, weatherLocation.latitude)
        contentValues.put(COLUMN_LONGITUDE, weatherLocation.longitude)

        if (weatherCurrentId >= 0L) {
            contentValues.put(COLUMN_CURRENT_WEATHER_ID, weatherCurrentId)
        }

        return contentValues
    }

    private fun weatherCurrentContent(weatherCurrent: RetrofitWeatherCurrent,
                                      weatherID: Long = -1L): ContentValues {
        val content = ContentValues()
        content.put(COLUMN_TIME, weatherCurrent.timeUTC)
        content.put(COLUMN_SUNRISE_TIME, weatherCurrent.sunriseTimeUTC)
        content.put(COLUMN_SUNSET_TIME, weatherCurrent.sunsetTimeUTC)
        content.put(COLUMN_ATMOSPHERIC_PRESSURE, weatherCurrent.atmosphericPressureHectopascal)
        content.put(COLUMN_HUMIDITY_PERCENT, weatherCurrent.humidityPercent)
        content.put(COLUMN_TEMPERATURE, weatherCurrent.temperatureKelvin)
        content.put(COLUMN_TEMPERATURE_FEELS, weatherCurrent.temperatureFeelsKelvin)

        if (weatherID >= 0L) {
            content.put(COLUMN_WEATHER_ID, weatherID)
        }

        return content
    }

    private fun weatherDailyContent(weatherDaily: RetrofitWeatherDaily,
                                    weatherLocationID: Int = -1, weatherID: Long = -1L,
                                    temperaturesID: Long = -1, temperaturesFeelsID: Long = -1L): ContentValues {
        val content = ContentValues()
        content.put(COLUMN_TIME, weatherDaily.timeUTC)
        content.put(COLUMN_SUNRISE_TIME, weatherDaily.sunriseTimeUTC)
        content.put(COLUMN_SUNSET_TIME, weatherDaily.sunsetTimeUTC)
        content.put(COLUMN_ATMOSPHERIC_PRESSURE, weatherDaily.atmosphericPressureHectopascal)
        content.put(COLUMN_HUMIDITY_PERCENT, weatherDaily.humidityPercent)

        if (weatherID >= 0L) {
            content.put(COLUMN_WEATHER_ID, weatherID)
        }

        if (temperaturesID >= 0L) {
            content.put(COLUMN_TEMPERATURE_ID, temperaturesID)
        }

        if (temperaturesFeelsID >= 0L) {
            content.put(COLUMN_TEMPERATURE_FEELS_ID, temperaturesFeelsID)
        }

        if (weatherLocationID >= 0) {
            content.put(COLUMN_WEATHER_LOCATION_ID, weatherLocationID)
        }

        return content
    }

    private fun weatherContent(weather: RetrofitWeather): ContentValues {
        val content = ContentValues()
        content.put(COLUMN_NAME, weather.name)
        content.put(COLUMN_DESCRIPTION, weather.description)
        content.put(COLUMN_ICON, weather.icon)
        return content
    }

    private fun temperatureDayContent(temperatureDay: RetrofitTemperatureDay): ContentValues {
        val content = ContentValues()
        content.put(COLUMN_DAY_TEMPERATURE, temperatureDay.dayTemperatureKelvin)
        content.put(COLUMN_NIGHT_TEMPERATURE, temperatureDay.nightTemperatureKelvin)
        content.put(COLUMN_EVENING_TEMPERATURE, temperatureDay.eveningTemperatureKelvin)
        content.put(COLUMN_MORNING_TEMPERATURE, temperatureDay.morningTemperatureKelvin)
        return content
    }

    private fun obtainWeatherCurrent(weatherCurrentID: Long, database: SQLiteDatabase): WeatherCurrent {
        var time = 0L
        var sunriseTime = 0L
        var sunsetTime = 0L
        var atmosphericPressure = 0.0
        var humidity = 0
        var weatherID = -1L
        var temperature = 0.0
        var temperatureFeels = 0.0

        val cursor = database.query(
                TABLE_WEATHER_CURRENT, // table
                arrayOf(COLUMN_TIME, COLUMN_SUNRISE_TIME, COLUMN_SUNSET_TIME, COLUMN_ATMOSPHERIC_PRESSURE,
                        COLUMN_HUMIDITY_PERCENT, COLUMN_WEATHER_ID, COLUMN_TEMPERATURE, COLUMN_TEMPERATURE_FEELS), // columns
                "$COLUMN_ID=?", //selection
                arrayOf(weatherCurrentID.toString()), // selectionArgs
                null, //groupBy
                null, //having
                null, //orderBy
                "1" //limit
        )

        if (cursor.moveToNext()) {
            time = cursor.getLong(0)
            sunriseTime = cursor.getLong(1)
            sunsetTime = cursor.getLong(2)
            atmosphericPressure = cursor.getDouble(3)
            humidity = cursor.getInt(4)
            weatherID = cursor.getLong(5)
            temperature = cursor.getDouble(6)
            temperatureFeels = cursor.getDouble(7)
        }

        cursor.close()

        val weather = this.obtainWeather(weatherID, database)
        return WeatherCurrent(
                time.utc, sunriseTime.utc, sunsetTime.utc,
                Temperature(temperature), Temperature(temperatureFeels),
                atmosphericPressure, humidity, weather)
    }

    private fun obtainWeatherDailyList(weatherLocationID: Int, database: SQLiteDatabase): List<WeatherDaily> {
        val weatherDailyList = ArrayList<WeatherDaily>()
        val cursor = database.query(
                TABLE_WEATHER_DAILY, // table
                arrayOf(COLUMN_TIME, COLUMN_SUNRISE_TIME, COLUMN_SUNSET_TIME, COLUMN_ATMOSPHERIC_PRESSURE,
                        COLUMN_HUMIDITY_PERCENT, COLUMN_WEATHER_ID, COLUMN_TEMPERATURE_ID, COLUMN_TEMPERATURE_FEELS_ID), // columns
                "$COLUMN_WEATHER_LOCATION_ID=?", //selection
                arrayOf(weatherLocationID.toString()), // selectionArgs
                null, //groupBy
                null, //having
                "$COLUMN_TIME ASC" //orderBy
        )

        while (cursor.moveToNext()) {
            weatherDailyList.add(this.weatherDailyFromCursor(cursor, database))
        }

        cursor.close()
        return weatherDailyList
    }

    private fun weatherDailyFromCursor(cursor: Cursor, database: SQLiteDatabase): WeatherDaily {
        val time = cursor.getLong(0).utc
        val sunrise = cursor.getLong(1).utc
        val sunset = cursor.getLong(2).utc
        val atmosphericPressure = cursor.getDouble(3)
        val humidity = cursor.getInt(4)
        val weather = this.obtainWeather(cursor.getLong(5), database)
        val temperatures = this.obtainTemperatureDay(cursor.getLong(6), database)
        val temperaturesFeels = this.obtainTemperatureDay(cursor.getLong(7), database)
        return WeatherDaily(time, sunrise, sunset, temperatures, temperaturesFeels, atmosphericPressure, humidity, weather)
    }

    private fun obtainWeather(weatherID: Long, database: SQLiteDatabase): Weather {
        var name = ""
        var description = ""
        var icon = ""

        val cursor = database.query(
                TABLE_WEATHER, // table
                arrayOf(COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_ICON), // columns
                "$COLUMN_ID=?", //selection
                arrayOf(weatherID.toString()), // selectionArgs
                null, //groupBy
                null, //having
                null, //orderBy
                "1" //limit
        )

        if (cursor.moveToNext()) {
            name = cursor.getString(0)
            description = cursor.getString(1)
            icon = cursor.getString(2)
        }

        cursor.close()
        return Weather(name, description, WeatherIcon.byReference(icon))
    }

    private fun obtainTemperatureDay(temperatureDayID: Long, database: SQLiteDatabase): TemperatureDay {
        var dayTemperature = 0.0
        var nightTemperature = 0.0
        var eveningTemperature = 0.0
        var morningTemperature = 0.0

        val cursor = database.query(
                TABLE_TEMPERATURE_DAY, // table
                arrayOf(COLUMN_DAY_TEMPERATURE, COLUMN_NIGHT_TEMPERATURE, COLUMN_EVENING_TEMPERATURE, COLUMN_MORNING_TEMPERATURE), // columns
                "$COLUMN_ID=?", //selection
                arrayOf(temperatureDayID.toString()), // selectionArgs
                null, //groupBy
                null, //having
                null, //orderBy
                "1" //limit
        )

        if (cursor.moveToNext()) {
            dayTemperature = cursor.getDouble(0)
            nightTemperature = cursor.getDouble(1)
            eveningTemperature = cursor.getDouble(2)
            morningTemperature = cursor.getDouble(3)
        }

        cursor.close()

        return TemperatureDay(Temperature(dayTemperature), Temperature(nightTemperature), Temperature(eveningTemperature), Temperature(morningTemperature))
    }
}
