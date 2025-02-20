package com.listingapp

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.listingapp.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppViewModel: ViewModel() {

    fun getWeather(app: ListApp, lat: Double, lon: Double, topAppBarState: MutableState<AppBarViewState>) {
        val loc = app.getLocationData()
        if (loc != null) {
            if (loc.isNotEmpty()) {
                val getLoc = loc.split("~~~")
                topAppBarState.value = AppBarViewState.getWeather(
                    title = "Listing App",
                    degree = getLoc[0].toInt(),
                    city = getLoc[3],
                    status = getLoc[1],
                    image = getLoc[2]
                )
            }
        }

        viewModelScope.launch(Dispatchers.Main) {
            val weather = ApiRepo.fetchWeather(app, lat, lon)
            val weatherCity = ApiRepo.fetchWeatherCity(app, lat, lon)
            if (weather!= null) {
                val weatherData = WeatherData.parseEntry(weather)
//                val weatherData = weather.objectArray("weather").map {
//                    WeatherData(
//                        temp = weather.getJSONObject("main").getInt("temp"),
//                        description = it.getString("description"),
//                        icon = it.getString("icon"),
//                    )
//                }.toTypedArray()
                val city = weatherCity?.getJSONObject(0)?.getString("name") ?: "UnKnown"
                topAppBarState.value = AppBarViewState.getWeather(
                    title = "Listing App",
                    degree = weatherData.temp,
                    city = city,
                    status = weatherData.description,
                    image = weatherData.icon
                )
                app.setLocationData(arrayOf(
                    weatherData.temp.toString(),
                    weatherData.description,
                    weatherData.icon,
                    city
                ))
            }
            else {
                if (loc != null) {
                    if (loc.isNotEmpty()) {val getLoc = loc.split("~~~")
                        topAppBarState.value = AppBarViewState.getWeather(
                            title = "Listing App",
                            degree = getLoc[0].toInt(),
                            city = getLoc[3],
                            status = getLoc[1],
                            image = getLoc[2]
                        )

                    }
                }
                else {
                    topAppBarState.value = AppBarViewState.getTitle(
                        title = "Listing App"
                    )
                }
            }
        }
    }

    fun UpdateTopBar(app: ListApp, topAppBarState: MutableState<AppBarViewState>) {
        viewModelScope.launch(Dispatchers.Main) {
            topAppBarState.value = AppBarViewState.getTitileWithBack("User Details")
        }
    }
}