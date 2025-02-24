package com.listingapp

import android.Manifest
import android.app.AlertDialog
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import org.json.JSONArray
import org.json.JSONObject

/**
 * Utility functions and constants for handling weather-related operations, logging, and device status.
 */

const val WeatherData = "WeatherData" // Constant for storing weather data key
const val LOG = "APPLIST" // Log tag for debugging purposes

/**
 * Logs a debug message with a predefined log tag.
 */
fun log(message: Any) {
    Log.d(LOG, message.toString())
}

/**
 * Converts a JSONArray to an array of JSONObject.
 */
fun JSONArray.objectArray(): Array<JSONObject> = (0 until this.length()).map {
    this.getJSONObject(it)
}.toTypedArray()

/**
 * Retrieves an array of JSONObject from a JSON object using a given key.
 * Returns an empty array if the key does not exist or an exception occurs.
 */
fun JSONObject.objectArray(arg: String): Array<JSONObject> = try {
    with(getJSONArray(arg)) { objectArray() }
} catch (e: Exception) {
    emptyArray()
}

/**
 * Maps a weather condition ID to a corresponding animation resource.
 */
fun weatherIcon(id: String): Int {
    return when (id) {
        "01d" -> R.raw.clear_sky
        "01n" -> R.raw.clear_sky_night
        "02d", "03d", "04d", "02n", "03n", "04n" -> R.raw.cloud
        "09d", "10d", "11d", "09n", "10n", "11n" -> R.raw.thunder
        else -> R.raw.cloudy
    }
}

/**
 * Checks whether the device has an active internet connection.
 */
fun Context.isInternetAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)
    return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

/**
 * Shows an alert dialog prompting the user to enable location services.
 */
fun showEnableLocationDialog(context: Context, onSettingsOpened: () -> Unit) {
    AlertDialog.Builder(context)
        .setTitle("Enable Location Services")
        .setMessage("Your location services are turned off. Please enable them for better functionality.")
        .setPositiveButton("Go to Settings") { _, _ ->
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            onSettingsOpened()
        }
        .setNegativeButton("Cancel") { _, _ ->
            onSettingsOpened()
        }
        .show()
}

/**
 * Checks if location services are enabled on the device.
 */
fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

/**
 * Fetches the current location using FusedLocationProviderClient.
 */
fun fetchLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationFetched: (Double, Double) -> Unit
) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED
    ) {
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token

                override fun isCancellationRequested() = false
            }
        ).addOnSuccessListener { location ->
            if (location != null) {
                onLocationFetched(location.latitude, location.longitude)
            } else {
                Log.d("Location", "Location is null")
            }
        }.addOnFailureListener { e ->
            Log.d("Location", "Failed to fetch location: ${e.message}")
        }
    }
}

/**
 * Sets the status bar color dynamically based on the current theme (dark or light mode).
 */
fun ComponentActivity.setStatusBarColor(color: Int) {
    val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
    val statusBarColor = ContextCompat.getColor(this, color)
    val whiteColor = ContextCompat.getColor(this, R.color.white)
    val blackColor = ContextCompat.getColor(this, R.color.black)

    // Ensure the window is prepared for system bar background changes
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

    // Detect dark mode state
    val isDarkMode = (getSystemService(Context.UI_MODE_SERVICE) as UiModeManager).nightMode == UiModeManager.MODE_NIGHT_YES

    // Apply edge-to-edge configuration
    enableEdgeToEdge(
        statusBarStyle = if (isDarkMode) SystemBarStyle.dark(statusBarColor) else SystemBarStyle.light(statusBarColor, statusBarColor),
        navigationBarStyle = if (isDarkMode) SystemBarStyle.dark(blackColor) else SystemBarStyle.light(whiteColor, whiteColor)
    )

    // Apply light icons on the status bar
    windowInsetsController.isAppearanceLightStatusBars = false
}


