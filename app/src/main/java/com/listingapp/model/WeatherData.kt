package com.listingapp.model

import org.json.JSONObject

data class WeatherData(
    val temp: Int,
    val description: String,
    val icon: String
) {
    companion object {
        fun parseEntry(json: JSONObject): WeatherData {
            val weather = json.getJSONArray("weather").getJSONObject(0)

            return WeatherData(
                temp = json.getJSONObject("main").getInt("temp"),
                description = weather.getString("description"),
                icon = weather.getString("icon")
            )
        }
    }
}
