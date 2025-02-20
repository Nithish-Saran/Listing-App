package com.listingapp.model

import com.listingapp.formatDate
import org.json.JSONObject

data class UserData(
    val gender: String,
    val firstName: String,
    val lastName: String,
    val city: String,
    val state: String,
    val country: String,
    val latitude: String,
    val longitude: String,
    val email: String,
    val dob: String,
    val age: Int,
    val phone: String,
    val cell: String,
    val pictureLarge: String,
    val pictureMedium: String,
    val pictureThumbnail: String,
) {
    companion object {
        fun parseData(json: JSONObject): MutableList<UserData> {
            val resultsArray = json.getJSONArray("results")
            val userList = mutableListOf<UserData>()

            for (i in 0 until resultsArray.length()) {
                val userObject = resultsArray.getJSONObject(i)

                val user = UserData(
                    gender = userObject.getString("gender"),
                    firstName = userObject.getJSONObject("name").getString("first"),
                    lastName = userObject.getJSONObject("name").getString("last"),
                    city = userObject.getJSONObject("location").getString("city"),
                    state = userObject.getJSONObject("location").getString("state"),
                    country = userObject.getJSONObject("location").getString("country"),
                    latitude = userObject.getJSONObject("location")
                        .getJSONObject("coordinates").getString("latitude"),
                    longitude = userObject.getJSONObject("location")
                        .getJSONObject("coordinates").getString("longitude"),
                    email = userObject.getString("email"),
                    dob = formatDate(userObject.getJSONObject("dob").getString("date")),
                    age = userObject.getJSONObject("dob").getInt("age"),
                    phone = userObject.getString("phone"),
                    cell = userObject.getString("cell"),
                    pictureLarge = userObject.getJSONObject("picture").getString("large"),
                    pictureMedium = userObject.getJSONObject("picture").getString("medium"),
                    pictureThumbnail = userObject.getJSONObject("picture").getString("thumbnail"),
                )

                userList.add(user)
            }

            return userList
        }
    }
}
