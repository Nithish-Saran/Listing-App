package com.listingapp

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.listingapp.db.entity.UserEntity
import com.listingapp.model.WeatherData
import com.listingapp.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel()  {

    private val _userDataState = MutableStateFlow<UserDataState.UserListState>(UserDataState.UserListState.Loading)
    val userDataState: StateFlow<UserDataState.UserListState> = _userDataState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _userList = MutableStateFlow<List<UserEntity>>(emptyList())
    val userList: StateFlow<List<UserEntity>> = _userList

    fun getLocalWeather(app: ListApp, lat: Double, lon: Double, topAppBarState: MutableState<AppBarViewState>) {
        val loc = app.getLocationData()
        if (loc != null) {
            if (loc.isNotEmpty()) {
                val getLoc = loc.split("~~~")
                topAppBarState.value = AppBarViewState.getWeather(
                    title = "Listing App",
                    degree = getLoc[0].toInt(),
                    city = getLoc[3],
                    status = getLoc[1],
                    image = getLoc[2]
                )
            }
        }

        viewModelScope.launch(Dispatchers.Main) {
            val weather = ApiRepo.fetchWeather(app, lat, lon)
            val weatherCity = ApiRepo.fetchWeatherCity(app, lat, lon)
            if (weather!= null) {
                val weatherData = WeatherData.parseEntry(weather)
                val city = weatherCity?.getJSONObject(0)?.getString("name") ?: "UnKnown"
                topAppBarState.value = AppBarViewState.getWeather(
                    title = "Listing App",
                    degree = weatherData.temp,
                    city = city,
                    status = weatherData.description,
                    image = weatherData.icon
                )
                app.setLocationData(arrayOf(
                    weatherData.temp.toString(),
                    weatherData.description,
                    weatherData.icon,
                    city
                ))
            }
            else {
                if (loc != null) {
                    if (loc.isNotEmpty()) {val getLoc = loc.split("~~~")
                        topAppBarState.value = AppBarViewState.getWeather(
                            title = "Listing App",
                            degree = getLoc[0].toInt(),
                            city = getLoc[3],
                            status = getLoc[1],
                            image = getLoc[2]
                        )
                    }
                }
                else {
                    topAppBarState.value = AppBarViewState.getTitle(
                        title = "Listing App"
                    )
                }
            }
        }
    }

    fun getUserWeatherData(
        app: ListApp,
        lat: Double,
        lon: Double,
        topAppBarState: MutableState<AppBarViewState>,
        onReturn: (Int, String, String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            Log.d("APPLIST", "lati : $lat")
            Log.d("APPLIST", "longi : $lon")
            val weather = ApiRepo.fetchWeather(app, lat, lon)
            Log.d("APPLIST", weather.toString())
            if (weather!= null) {
                val weatherData = WeatherData.parseEntry(weather)
//                val city = if (weatherCity != null) {
//                    weatherCity.getJSONObject(0)?.getString("name")
//                }
//                else {
//                    "Unkown"
//                }
                onReturn(weatherData.temp, weatherData.icon, weatherData.description)
                topAppBarState.value = AppBarViewState.getTitileWithBack("User Details")
            }
        }
    }

    fun addUser() {
        //_userDataState.value = UserDataState.UserListState.Loading
        viewModelScope.launch {
            val users = ApiRepo.fetchUser(app = ListApp())
            val userList = mutableListOf<UserEntity>()
            if (_userDataState.value is UserDataState.UserListState.Success) {
                val data = (_userDataState.value as UserDataState.UserListState.Success).data
                userList.addAll(data)
            }
            if (users != null) {
                users.objectArray("results").map {
                    val user = UserEntity.parseData(it)
                    userList.add(user)
                    repository.insertUser(user)
                }
                _userDataState.value = UserDataState.UserListState.Success(userList.toTypedArray())
            }
            else {
                Log.d("WHILOGS", "error")
                _userDataState.value = UserDataState.UserListState.Error
            }
        }
    }

    fun getAllUser() {
        viewModelScope.launch {
            Log.d("WHILOGS", repository.allUsers().size.toString())
            _userDataState.value =
                UserDataState.UserListState.Success(repository.allUsers().toTypedArray())
        }
    }

    fun searchUsers(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            repository.searchUsers(query)
                .collect { users -> _userList.value = users }
        }
    }

    fun getUserById(userId: String): Flow<UserEntity> {
        return repository.getUserById(userId)
    }
}