package com.listingapp.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.listingapp.viewstate.AppBarViewState
import com.listingapp.ListApp
import com.listingapp.viewstate.UserDataState
import com.listingapp.db.entity.UserEntity
import com.listingapp.isInternetAvailable
import com.listingapp.log
import com.listingapp.model.WeatherData
import com.listingapp.objectArray
import com.listingapp.repo.ApiRepo
import com.listingapp.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: UserRepository,
) : ViewModel() {

    // Flags to manage API calls and pagination
    private var apiCalled = false
    private var currentPage = 0
    private var pageSize = 10
    private var isLoading = false

    // StateFlow to track user list state (loading, success, no data, etc.)
    private val _userDataState =
        MutableStateFlow<UserDataState.UserListState>(UserDataState.UserListState.Loading)
    val userDataState: StateFlow<UserDataState.UserListState> = _userDataState.asStateFlow()

    // List to store user data
    private val _usersList = mutableStateListOf<UserEntity>()
    val usersList: List<UserEntity> get() = _usersList

    /**
     * Searches users based on the given query string.
     * Updates the list if users are found; otherwise, sets the state to NoData.
     */
    fun searchUsers(query: String) {
        viewModelScope.launch {
            repository.searchUsers(query).collectLatest { users ->
                if (users.isNotEmpty()) {
                    _usersList.clear()
                    _usersList.addAll(users)
                    _userDataState.value = UserDataState.UserListState.Success
                } else {
                    _userDataState.value = UserDataState.UserListState.NoData
                }
            }
        }
    }

    /**
     * Fetches the weather data for a given latitude and longitude.
     * Updates the top bar with the weather information.
     */
    fun getWeather(
        app: ListApp,
        lat: Double,
        lon: Double,
        topBarState: MutableState<AppBarViewState>,
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            topBarState.value = AppBarViewState.getTitle("Listing App")

            if (app.getLocationData()?.isNotEmpty() == true) {
                getLocalWeather(app, topBarState)
            } else {
                val weather = ApiRepo.fetchWeather(app, lat, lon)
                val weatherCity = ApiRepo.fetchWeatherCity(app, lat, lon)

                if (weather != null) {
                    val weatherData = WeatherData.parseEntry(weather)
                    val city = if (weatherCity != null && weatherCity.length() > 0) {
                        weatherCity.getJSONObject(0).optString("name", "UnKnown")
                    } else {
                        "UnKnown"
                    }

                    // Update the top bar with weather info
                    topBarState.value = AppBarViewState.getLocalWeather(
                        title = "Listing App",
                        degree = weatherData.temp,
                        city = city,
                        status = weatherData.description,
                        image = weatherData.icon
                    )

                    // Cache weather data locally
                    app.setLocationData(
                        arrayOf(
                            weatherData.temp.toString(),
                            weatherData.description,
                            weatherData.icon,
                            city
                        )
                    )
                } else {
                    topBarState.value = AppBarViewState.getTitle("Listing App")
                }
            }
        }
    }

    //Retrieves locally stored weather data and updates the top bar.
    fun getLocalWeather(app: ListApp, topBarState: MutableState<AppBarViewState>) {
        val localData = app.getLocationData()
        localData?.let { weather ->
            if (weather.isNotEmpty()) {
                val weatherData = weather.split("~~~")
                topBarState.value = AppBarViewState.getLocalWeather(
                    title = "Listing App",
                    degree = weatherData[0].toInt(),
                    city = weatherData[3],
                    status = weatherData[1],
                    image = weatherData[2]
                )
            } else {
                topBarState.value = AppBarViewState.getTitle("Listing App")
            }
        }
    }

    // Fetches the weather data for a specific user location and returns it via a callback.
    fun getUserWeatherData(
        app: ListApp,
        lat: Double,
        lon: Double,
        onReturn: (Int, String, String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            val weather = ApiRepo.fetchWeather(app, lat, lon)
            if (weather != null) {
                val weatherData = WeatherData.parseEntry(weather)
                onReturn(weatherData.temp, weatherData.icon, weatherData.description)
            }
        }
    }

    //Fetches user data from the API and inserts it into the database if it doesn't exist.
    fun addNewUsers() {
        viewModelScope.launch {
            if (repository.getTotalUserCount() == 0 && !apiCalled) {
                apiCalled = true
                _userDataState.value = UserDataState.UserListState.Loading
                val users = ApiRepo.fetchUser(app = ListApp())

                users?.objectArray("results")?.map {
                    val user = UserEntity.parseData(it)
                    repository.insertUser(user)
                }
                loadMoreItems()
            }
        }
    }

    // Fetches users from the database and updates the user list state.
    fun fetchUsers(isInternetAvailable: Boolean = true) {
        viewModelScope.launch {
            if (repository.getTotalUserCount() == 0 && !isInternetAvailable) {
                _userDataState.value = UserDataState.UserListState.NoNetwork
            }
            _usersList.clear()
            currentPage = 0
            loadMoreItems()
        }
    }

    // Loads the next set of users for pagination.
    fun loadMoreItems() {
        if (isLoading) return
        isLoading = true

        viewModelScope.launch {
            if (repository.getTotalUserCount() > (currentPage * pageSize)) {
                val newItems = repository.getUsers((currentPage * pageSize), pageSize)
                if (newItems.isNotEmpty()) {
                    _usersList.addAll(newItems)
                    currentPage++
                    _userDataState.value = UserDataState.UserListState.Success
                } else {
                    _userDataState.value = UserDataState.UserListState.NoData
                }
            }
            isLoading = false
        }
    }

    // Retrieves a user by their ID.
    fun getUserById(userId: String): Flow<UserEntity> {
        return repository.getUserById(userId)
    }
}
