package com.listingapp.model

data class ApiLocation(
    val city: String,
    val state: String,
    val country: String,
    val postcode: Int,
    val coordinates: ApiCoordinates,
)