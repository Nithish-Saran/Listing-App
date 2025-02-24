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

    /**
     * Stores location-based weather data in SharedPreferences.
     *
     * @param weatherData An array of weather-related data, stored as a concatenated string
     * using '~~~' as a delimiter.
     */
    fun setLocationData(weatherData: Array<String>) {
        instance.getSharedPreferences(WeatherData, Context.MODE_PRIVATE).edit().apply {
            putString(WeatherData, weatherData.joinToString("~~~"))
            apply()
        }
    }

    /**
     * Retrieves the stored weather data from SharedPreferences.
     *
     * @return The stored weather data as a concatenated string or an empty string if no data is found.
     */
    fun getLocationData(): String? {
        return instance.getSharedPreferences(WeatherData, Context.MODE_PRIVATE)
            .getString(WeatherData, "")
    }
}
