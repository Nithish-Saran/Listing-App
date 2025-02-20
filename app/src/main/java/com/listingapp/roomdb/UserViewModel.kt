package com.listingapp.roomdb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.listingapp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel()
{

    val userData: StateFlow<User?> = repository.getUser()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun fetchAndStoreUser(apiService: ApiService) {
        viewModelScope.launch {
            repository.getUser().collectLatest { storedUser ->
                if (storedUser == null) { // Fetch only if DB is empty
                    val response = apiService.getUserProfile()
                    response.results.firstOrNull()?.let { apiUser ->
                        val user = User(
                            gender = apiUser.gender,
                            title = apiUser.name.title,
                            firstName = apiUser.name.first,
                            lastName = apiUser.name.last,
                            city = apiUser.location.city,
                            state = apiUser.location.state,
                            country = apiUser.location.country,
                            postcode = apiUser.location.postcode,
                            latitude = apiUser.location.coordinates.latitude,
                            longitude = apiUser.location.coordinates.longitude,
                            email = apiUser.email,
                            dob = apiUser.dob.date,
                            age = apiUser.dob.age,
                            phone = apiUser.phone,
                            cell = apiUser.cell,
                            pictureLarge = apiUser.picture.large,
                            pictureMedium = apiUser.picture.medium,
                            pictureThumbnail = apiUser.picture.thumbnail,
                            nationality = apiUser.nat,
                            uuid = 0.toString()
                        )
                        repository.insertUser(user)
                    }
                }
            }
        }
    }

}
