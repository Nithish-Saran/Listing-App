package com.listingapp

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: UserRepository,
) : ViewModel() {

    var apiCalled = false

    private val _userDataState =
        MutableStateFlow<UserDataState.UserListState>(UserDataState.UserListState.Loading)
    val userDataState: StateFlow<UserDataState.UserListState> = _userDataState.asStateFlow()

    private val _usersList = mutableStateListOf<UserEntity>()
    val usersList: List<UserEntity> get() = _usersList

    private var _totalUserCount by mutableStateOf(0)
    val totalUserCount: Int get() = _totalUserCount

    init {
        viewModelScope.launch {
            _totalUserCount = repository.getTotalUserCount()
        }
    }


    fun searchUsers(query: String) {
        viewModelScope.launch {
            repository.searchUsers(query).collectLatest { users ->
                _userDataState.value = if (users.isNotEmpty()) {
                    UserDataState.UserListState.Success(users.toTypedArray())
                } else {
                    UserDataState.UserListState.NoData
                }
            }
        }
    }

    fun getWeather(
        app: ListApp,
        lat: Double,
        lon: Double,
        onReturn: (Int, String, String, String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            val weather = ApiRepo.fetchWeather(app, lat, lon)
            val weatherCity = ApiRepo.fetchWeatherCity(app, lat, lon)
            if (weather != null) {
                val weatherData = WeatherData.parseEntry(weather)
                val city = weatherCity?.getJSONObject(0)?.getString("name") ?: "UnKnown"
                onReturn(weatherData.temp, weatherData.icon, weatherData.description, city)
//                app.setLocationData(arrayOf(
//                    weatherData.temp.toString(),
//                    weatherData.description,
//                    weatherData.icon,
//                    city
//                ))
            }
        }
    }

    fun localWeather(app: ListApp, onReturn: (List<String>) -> Unit) {
        val loc = app.getLocationData()
        loc?.takeIf { it.isNotEmpty() }?.let {
            val getLoc = it.split("~~~")
            onReturn(getLoc)
        }
    }

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

    fun addUser() {
        viewModelScope.launch {
            _userDataState.value = UserDataState.UserListState.Loading
            val users = ApiRepo.fetchUser(app = ListApp())

            users?.objectArray("results")?.map {
                val user = UserEntity.parseData(it)
                repository.insertUser(user)
            }
            val localData = repository.allUsers()
            //val localData = repository.getUsers(0, 25)
            _userDataState.value = if (localData.isNotEmpty()) {
                UserDataState.UserListState.Success(localData.toTypedArray())
            } else UserDataState.UserListState.NoData
        }
    }

    fun getAllUser() {
        viewModelScope.launch {
            _userDataState.value = UserDataState.UserListState.Loading

            val users = repository.allUsers()
            if (users.isNotEmpty()) {
                _userDataState.value = UserDataState.UserListState.Success(users.toTypedArray())
            } else {
                addUser()
            }
        }
    }

    /*  fun getAllUsers(offset: Int, pageSize: Int) {
          viewModelScope.launch {
              //val userList = mutableListOf<UserEntity>()
  //            if (repository.getTotalUserCount() >= _usersList.size) {
  //                _usersList += repository.getUsers(offset, limit)
  //            }
  //            _userDataState.value = UserDataState.UserListState.Success(emptyArray())


  //            val totalCount = repository.getTotalUserCount()
  //            if (_usersList.size < totalCount) {
  //                val newUsers = repository.getUsers(offset, pageSize)
  //                if (newUsers.isNotEmpty()) {
  //                    _usersList.addAll(newUsers)
  //                }
  //            }
  //            // Update state with the aggregated list
  //            _userDataState.value = UserDataState.UserListState.Success(_usersList.toTypedArray())

              _userDataState.value = UserDataState.UserListState.Loading
              val totalCount = repository.getTotalUserCount()
              if (_usersList.size < totalCount) {
                  val newUsers = repository.getUsers(offset, pageSize)
                  if (newUsers.isNotEmpty()) {
                      _usersList.addAll(newUsers)
                      _userDataState.value = UserDataState.UserListState.Success(_usersList.toTypedArray())
                  }
                  else {
                      addUser()
                  }
              }
              // Update state with the aggregated list
             // _userDataState.value = UserDataState.UserListState.Success(_usersList.toTypedArray())
          }
      }*/

    /*fun addUser() {
        viewModelScope.launch {
            _userDataState.value = UserDataState.UserListState.Loading
            val users = ApiRepo.fetchUser(app = ListApp())
            Log.d("WHILOGS", "API returned: ${users?.objectArray("results")?.size} items")

            users?.objectArray("results")?.forEach { item ->
                val user = UserEntity.parseData(item)
                repository.insertUser(user)
                // Optionally, you could also add to _usersList here if you want immediate feedback:
                _usersList.add(user)
            }

            // After inserting, query the first page (or updated data)
            val localData = repository.getUsers(0, 25)
            Log.d("WHILOGS", "Local data size after API call: ${localData.size}")
            _userDataState.value = if (localData.isNotEmpty()) {
                // Update _usersList if needed
                _usersList.clear()
                _usersList.addAll(localData)
                UserDataState.UserListState.Success(localData.toTypedArray())
            } else {
                UserDataState.UserListState.NoData
            }
        }
    }*/

    fun getAllUsers(offset: Int, pageSize: Int) {
        viewModelScope.launch {
            // Indicate loading state first
            _userDataState.value = UserDataState.UserListState.Loading

            // Get the total available count from Room
            val totalCount = repository.getTotalUserCount()
            Log.d("WHILOGS", "Total count from DB: $totalCount, loaded: ${_usersList.size}")

            // If our accumulated list size is less than the total count,
            // then fetch more data.
            if (_usersList.size < totalCount) {
                val newUsers = repository.getUsers(offset, pageSize)
                Log.d(
                    "WHILOGS",
                    "Fetched ${newUsers.size} new users from DB with offset $offset, pageSize $pageSize"
                )
                if (newUsers.isNotEmpty()) {
                    _usersList.addAll(newUsers)
                    // Update state with the accumulated list
                    _userDataState.value =
                        UserDataState.UserListState.Success(_usersList.toTypedArray())
                } else {
                    // If no data is returned from local DB, try to load from API
                    addUser()
                }
            } else {
                // If we've loaded everything, update the state
                _userDataState.value =
                    UserDataState.UserListState.Success(_usersList.toTypedArray())
            }
        }
    }

    fun getUserById(userId: String): Flow<UserEntity> {
        return repository.getUserById(userId)
    }
}