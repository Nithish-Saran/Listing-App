package com.listingapp.roomdb

import com.listingapp.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/*class UserRepository(private val userDao: UserDao) {

    fun getUser(): Flow<User?> = userDao.getUser()

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }
}*/

class UserRepository @Inject constructor(private val userDao: UserDao) {
    fun getUser(): Flow<User?> = userDao.getUser()
    suspend fun insertUser(user: User) = userDao.insertUser(user)
}

