package com.listingapp

/**
 * Object to hold constant values used throughout the application.
 * These values include API keys and endpoint URLs.
 */
object Constant {
    /**
     * Base URL for fetching weather details from OpenWeatherMap API.
     * Requires parameters such as latitude, longitude, and API key.
     */
    val WEATHER_API = "https://api.openweathermap.org/data/2.5/weather?"

    //Base URL for reverse geolocation lookup to fetch city details based on latitude and longitude.
    val CITY_API = "https://api.openweathermap.org/geo/1.0/reverse?"

    /**
     * API endpoint to fetch a list of random users.
     * Returns a list of 100 users.
     */
    val USER_API = "https://randomuser.me/api/?results=100"
}
