package com.listingapp

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

