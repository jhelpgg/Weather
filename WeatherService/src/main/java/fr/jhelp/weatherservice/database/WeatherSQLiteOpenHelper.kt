package fr.jhelp.weatherservice.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import fr.jhelp.weatherservice.BuildConfig

internal class WeatherSQLiteOpenHelper(context: Context) :
        SQLiteOpenHelper(context, "WeatherDatabase", null, BuildConfig.WEATHER_DATABASE_VERSION) {

    override fun onConfigure(database: SQLiteDatabase) {
        // Activate foreign keys
        database.execSQL("PRAGMA foreign_keys = ON")
        super.onConfigure(database)
    }

    override fun onCreate(database: SQLiteDatabase) {
        this.createTemperatureDayTable(database)
        this.createWeatherTable(database)
        this.createWeatherCurrentTable(database)
        this.createWeatherLocationTable(database)
        this.createWeatherDailyTable(database)
    }

    // For now we have only one database version, so no upgrade will happen
    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

    private fun createTemperatureDayTable(database: SQLiteDatabase) {
        database.execSQL(
                """
                    CREATE TABLE $TABLE_TEMPERATURE_DAY
                    (
                        $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        $COLUMN_DAY_TEMPERATURE NUMBER DEFAULT 0,
                        $COLUMN_NIGHT_TEMPERATURE NUMBER DEFAULT 0,
                        $COLUMN_EVENING_TEMPERATURE NUMBER DEFAULT 0,
                        $COLUMN_MORNING_TEMPERATURE NUMBER DEFAULT 0
                    )
                """.trimIndent()
        )
    }

    private fun createWeatherTable(database: SQLiteDatabase) {
        database.execSQL(
                """
                    CREATE TABLE $TABLE_WEATHER
                    (
                        $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        $COLUMN_NAME TEXT DEFAULT "",
                        $COLUMN_DESCRIPTION TEXT DEFAULT "",
                        $COLUMN_ICON TEXT DEFAULT ""
                    )
                """.trimIndent()
        )
    }

    private fun createWeatherCurrentTable(database: SQLiteDatabase) {
        database.execSQL(
                """
                    CREATE TABLE $TABLE_WEATHER_CURRENT
                    (
                        $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        $COLUMN_TIME INTEGER DEFAULT 0,
                        $COLUMN_SUNRISE_TIME INTEGER DEFAULT 0,
                        $COLUMN_SUNSET_TIME INTEGER DEFAULT 0,
                        $COLUMN_ATMOSPHERIC_PRESSURE NUMBER DEFAULT 0,
                        $COLUMN_HUMIDITY_PERCENT INTEGER DEFAULT 0,
                        $COLUMN_WEATHER_ID INTEGER NOT NULL,
                        $COLUMN_TEMPERATURE NUMBER DEFAULT 0,
                        $COLUMN_TEMPERATURE_FEELS NUMBER DEFAULT 0,
                        FOREIGN KEY ($COLUMN_WEATHER_ID) REFERENCES $TABLE_WEATHER($COLUMN_ID) ON DELETE CASCADE                        
                    )
                """.trimIndent()
        )
    }

    private fun createWeatherDailyTable(database: SQLiteDatabase) {
        database.execSQL(
                """
                    CREATE TABLE $TABLE_WEATHER_DAILY
                    (
                        $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                        $COLUMN_TIME INTEGER DEFAULT 0,
                        $COLUMN_SUNRISE_TIME INTEGER DEFAULT 0,
                        $COLUMN_SUNSET_TIME INTEGER DEFAULT 0,
                        $COLUMN_ATMOSPHERIC_PRESSURE NUMBER DEFAULT 0,
                        $COLUMN_HUMIDITY_PERCENT INTEGER DEFAULT 0,
                        $COLUMN_WEATHER_ID INTEGER NOT NULL,
                        $COLUMN_TEMPERATURE_ID INTEGER NOT NULL,
                        $COLUMN_TEMPERATURE_FEELS_ID INTEGER NOT NULL,
                        $COLUMN_WEATHER_LOCATION_ID INTEGER NOT NULL,
                        FOREIGN KEY ($COLUMN_WEATHER_ID) REFERENCES $TABLE_WEATHER($COLUMN_ID) ON DELETE CASCADE,                        
                        FOREIGN KEY ($COLUMN_TEMPERATURE_ID) REFERENCES $TABLE_TEMPERATURE_DAY($COLUMN_ID) ON DELETE CASCADE,                        
                        FOREIGN KEY ($COLUMN_TEMPERATURE_FEELS_ID) REFERENCES $TABLE_TEMPERATURE_DAY($COLUMN_ID) ON DELETE CASCADE,                        
                        FOREIGN KEY ($COLUMN_WEATHER_LOCATION_ID) REFERENCES $TABLE_WEATHER_LOCATION($COLUMN_ID) ON DELETE CASCADE                        
                    )
                """.trimIndent()
        )
    }

    private fun createWeatherLocationTable(database: SQLiteDatabase) {
        database.execSQL(
                """
                    CREATE TABLE $TABLE_WEATHER_LOCATION
                    (
                        $COLUMN_ID INTEGER PRIMARY KEY,
                        $COLUMN_LATITUDE NUMBER DEFAULT 0,
                        $COLUMN_LONGITUDE NUMBER DEFAULT 0,
                        $COLUMN_CURRENT_WEATHER_ID INTEGER NOT NULL,
                        FOREIGN KEY ($COLUMN_CURRENT_WEATHER_ID) REFERENCES $TABLE_WEATHER_CURRENT($COLUMN_ID) ON DELETE CASCADE                        
                    )
                """.trimIndent()
        )
    }
}