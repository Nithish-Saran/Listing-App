package com.listingapp.repo

import com.listingapp.db.dao.UserDao
import com.listingapp.db.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(private val userDao: UserDao) {

    suspend fun insertUser(user: UserEntity) {
        userDao.insertOrUpdate(user)
    }

    suspend fun allUsers() =  withContext(Dispatchers.IO) {
        userDao.getAllUsers()
    }

    fun searchUsers(query: String): Flow<List<UserEntity>> {
        return userDao.searchUsers(query)
    }

    fun getUserById(userId: String): Flow<UserEntity> {
        return userDao.getUserById(userId)
    }
}
