package com.listingapp.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject

/** Represents a user entity in the database. */
@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey val uuid: String,  // Unique ID for the user
    val gender: String,            // User's gender
    val firstName: String,         // First name
    val lastName: String,          // Last name
    val city: String,              // City of residence
    val state: String,             // State of residence
    val country: String,           // Country of residence
    val latitude: Double,          // Latitude coordinate
    val longitude: Double,         // Longitude coordinate
    val email: String,             // Email address
    val dob: String,               // Date of birth
    val age: Int,                  // Age
    val phone: String,             // Phone number
    val pictureLarge: String,      // Large profile picture URL
    val pictureMedium: String,     // Medium profile picture URL
    val pictureThumbnail: String   // Thumbnail profile picture URL
) {
    companion object {
        /** Parses JSON data into a UserEntity object. */
        fun parseData(json: JSONObject) = UserEntity(
            uuid = json.getJSONObject("login").getString("uuid"),
            gender = json.getString("gender"),
            firstName = json.getJSONObject("name").getString("first"),
            lastName = json.getJSONObject("name").getString("last"),
            city = json.getJSONObject("location").getString("city"),
            state = json.getJSONObject("location").getString("state"),
            country = json.getJSONObject("location").getString("country"),
            latitude = json.getJSONObject("location")
                .getJSONObject("coordinates").getString("latitude").toDouble(),
            longitude = json.getJSONObject("location")
                .getJSONObject("coordinates").getString("longitude").toDouble(),
            email = json.getString("email"),
            dob = json.getJSONObject("dob").getString("date"),
            age = json.getJSONObject("dob").getInt("age"),
            phone = json.getString("phone"),
            pictureLarge = json.getJSONObject("picture").getString("large"),
            pictureMedium = json.getJSONObject("picture").getString("medium"),
            pictureThumbnail = json.getJSONObject("picture").getString("thumbnail"),
        )
    }
}

