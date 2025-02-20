package com.listingapp.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey val uuid: String,
    val gender: String,
    val firstName: String,
    val lastName: String,
    val city: String,
    val state: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val email: String,
    val dob: String,
    val age: Int,
    val phone: String,
    val pictureLarge: String,
    val pictureMedium: String,
    val pictureThumbnail: String,
) {
    companion object {
        fun parseData(json: JSONObject) =  UserEntity (
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
