package com.listingapp

import androidx.compose.runtime.getValue
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

const val WeatherData = "WeatherData"
const val AppPref = "AppPref"

fun JSONArray.objectArray(): Array<JSONObject> = (0 until this.length()).map {
    this.getJSONObject(it)
}.toTypedArray()

fun JSONObject.objectArray(arg: String): Array<JSONObject> = try {
    with(getJSONArray(arg)) { objectArray() }
} catch (e: Exception) {
    emptyArray()
}

fun formatDate(dateStr: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateStr)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        dateStr
    }
}

fun weatherIcon(id: String): Int {
    return when (id) {
        "01d",-> R.raw.clear_sky
        "01n"-> R.raw.clear_sky_night
        "02d", "03d", "04d", "02n", "03n", "04n" -> R.raw.cloud
        "09d", "10d", "11d", "09n", "10n", "11n" -> R.raw.thunder
        else -> R.raw.cloudy
    }
}


