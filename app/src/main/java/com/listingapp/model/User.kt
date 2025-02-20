package com.listingapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey @ColumnInfo(name = "user_id") val uuid: String,
    val gender: String,
    val title: String,
    val firstName: String,
    val lastName: String,
    val city: String,
    val state: String,
    val country: String,
    val postcode: Int,
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
    val nationality: String
)

