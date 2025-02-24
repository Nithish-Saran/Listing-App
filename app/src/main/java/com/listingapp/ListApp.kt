package com.listingapp

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

/**
 * Main application class annotated with @HiltAndroidApp to support Dependency Injection with Hilt.
 * This class serves as the entry point of the application and provides global access to
 * application-wide dependencies.
 */
@HiltAndroidApp
class ListApp : Application() {

    companion object {
        /**
         * Singleton instance of the ListApp application class.
         * This allows access to application-level context and functions.
         */
        lateinit var instance: ListApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

//    /**
//     * Stores location-based weather data in SharedPreferences.
//     *
//     * @param weatherData An array of weather-related data, stored as a concatenated string
//     * using '~~~' as a delimiter.
//     */
//    fun setLocationData(weatherData: Array<String>) {
//        instance.getSharedPreferences(WeatherData, Context.MODE_PRIVATE).edit().apply {
//            putString(WeatherData, weatherData.joinToString("~~~"))
//            apply()
//        }
//    }
//
//    /**
//     * Retrieves the stored weather data from SharedPreferences.
//     *
//     * @return The stored weather data as a concatenated string or an empty string if no data is found.
//     */
//    fun getLocationData(): String? {
//        return instance.getSharedPreferences(WeatherData, Context.MODE_PRIVATE)
//            .getString(WeatherData, "")
//    }

    private val WEATHER_KEY = "WeatherData"
    private val LOCATION_KEY = "LastKnownLocation"

     /**
     * Stores location-based weather data in SharedPreferences.
     * @param weatherData An array of weather-related data, stored as a concatenated string
     * using '~~~' as a delimiter.
     */
    fun setLocationData(weatherData: Array<String>, lat: Double, lon: Double) {
        instance.getSharedPreferences(WEATHER_KEY, Context.MODE_PRIVATE).edit().apply {
            putString(WEATHER_KEY, weatherData.joinToString("~~~"))
            putString(LOCATION_KEY, "$lat,$lon") // Store latitude and longitude as a string
            apply()
        }
    }

    /**
     * Retrieves the stored weather data from SharedPreferences.
     * @return The stored weather data as a concatenated string or null if no data is found.
     */
    fun getLocationData(): String? {
        return instance.getSharedPreferences(WEATHER_KEY, Context.MODE_PRIVATE)
            .getString(WEATHER_KEY, null)
    }

    /**
     * Retrieves the last known location from SharedPreferences.
     * @return A Pair of latitude and longitude, or null if no location is stored.
     */
    fun getLastKnownLocation(): Pair<Double, Double>? {
        val storedLocation = instance.getSharedPreferences(WEATHER_KEY, Context.MODE_PRIVATE)
            .getString(LOCATION_KEY, null)

        return storedLocation?.split(",")?.let {
            if (it.size == 2) Pair(it[0].toDouble(), it[1].toDouble()) else null
        }
    }

    /**
     * Stores the last known location (latitude and longitude) in SharedPreferences.
     * @param lat The latitude value to store.
     * @param lon The longitude value to store.
     */
    fun setLastKnownLocation(lat: Double, lon: Double) {
        instance.getSharedPreferences(WEATHER_KEY, Context.MODE_PRIVATE).edit().apply {
            putString(LOCATION_KEY, "$lat,$lon")
            apply()
        }
    }


}
