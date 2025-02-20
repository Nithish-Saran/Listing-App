package com.listingapp.roomdb

import com.listingapp.model.UserResponse
import retrofit2.http.GET

interface ApiService {
    @GET("https://randomuser.me/api/")
    suspend fun getUserProfile(): UserResponse
}
