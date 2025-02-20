package com.listingapp

import android.app.Application
import android.content.Context

class ListApp: Application() {

    companion object {
        lateinit var instance: ListApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun setLocationData(weatherData: Array<String>) {
        instance.getSharedPreferences(WeatherData, Context.MODE_PRIVATE).edit().apply {
            putString(WeatherData, weatherData.joinToString("~~~"))
            apply()
        }
    }

    fun getLocationData(): String? {
        return instance.getSharedPreferences(WeatherData, Context.MODE_PRIVATE)
            .getString(WeatherData, "")
    }
}