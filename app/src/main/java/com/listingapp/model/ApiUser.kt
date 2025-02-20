package com.listingapp.model

data class ApiUser(
    val gender: String,
    val name: ApiName,
    val location: ApiLocation,
    val email: String,
    val dob: ApiDOB,
    val phone: String,
    val cell: String,
    val picture: ApiPicture,
    val nat: String
)

